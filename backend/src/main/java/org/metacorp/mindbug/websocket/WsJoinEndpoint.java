package org.metacorp.mindbug.websocket;

import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketListener;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.GameMode;
import org.metacorp.mindbug.model.card.CardSetName;
import org.metacorp.mindbug.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.stream.Collectors;

public class WsJoinEndpoint extends WebSocketApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsJoinEndpoint.class);

    private final Map<GameMode, Map<CardSetName, Queue<JoinWebSocket>>> joinQueues = new HashMap<>();

    // TODO Create a map or a structure to store player responses and start the game when both players have confirmed

    private final GameService gameService;

    /**
     * Constructor
     *
     * @param gameService the Game service
     */
    public WsJoinEndpoint(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket requestPacket, WebSocketListener... listeners) {
        return new JoinWebSocket(handler, requestPacket, listeners);
    }

    @Override
    public void onConnect(WebSocket rawSocket) {
        super.onConnect(rawSocket);

        JoinWebSocket socket = (JoinWebSocket) rawSocket;

        GameMode gameMode = socket.getMode();
        int requiredPlayersCount = gameMode.getRequiredPlayersCount();
        joinQueues.computeIfAbsent(gameMode, _ -> new HashMap<>());

        Map<CardSetName, Queue<JoinWebSocket>> modeQueues = joinQueues.get(gameMode);

        for (CardSetName set : socket.getSets()) {
            if (set != null) {
                Queue<JoinWebSocket> setQueue = modeQueues.get(set);
                if (setQueue == null) {
                    modeQueues.put(set, new LinkedList<>(Collections.singleton(socket)));
                } else {
                    if (setQueue.size() >= requiredPlayersCount - 1) {
                        String playerId = socket.getPlayerId();

                        List<JoinWebSocket> gameSockets = new ArrayList<>();
                        for (int i = 1; i < requiredPlayersCount; i++) {
                            gameSockets.add(setQueue.poll());
                        }

                        if (gameSockets.stream().noneMatch(otherPlayerSocket -> otherPlayerSocket == null || playerId.equals(otherPlayerSocket.getPlayerId()))) {
                            gameSockets.add(socket);
                            createGame(gameSockets, set);
                        }
                    } else {
                        setQueue.add(socket);
                    }
                }
            }
        }
    }

    /**
     * Create a new game
     *
     * @param sockets the player WebSockets
     * @param set     the card set to use
     */
    private void createGame(List<JoinWebSocket> sockets, CardSetName set) {
        try {
            Game game = gameService.createGame(sockets.stream().map(socket -> UUID.fromString(socket.getPlayerId())).collect(Collectors.toSet()), set);

            sockets.forEach(player -> player.send(game.getUuid().toString()));
        } catch (UnknownPlayerException | CardSetException e) {
            LOGGER.warn("Unable to start a new game", e);
        }

        //TODO Maybe implement a timeout system
    }

    @Override
    public void onMessage(WebSocket socket, String text) {
        // Nothing to do for the moment
    }

    @Override
    public void onClose(WebSocket rawSocket, DataFrame frame) {
        JoinWebSocket socket = (JoinWebSocket) rawSocket;
        GameMode gameMode = socket.getMode();

        for (Queue<JoinWebSocket> joinQueue : joinQueues.get(gameMode).values()) {
            List<JoinWebSocket> matchingSessions = joinQueue.stream()
                    .filter(currentSession -> socket.getPlayerId().equals(currentSession.getPlayerId()))
                    .toList();
            joinQueue.removeAll(matchingSessions);
        }

        LOGGER.debug("Player {} ({}) left waiting queue", socket.getPlayerName(), socket.getPlayerId());

        super.onClose(socket, frame);
    }
}