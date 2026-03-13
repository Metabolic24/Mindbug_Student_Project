package org.metacorp.mindbug.websocket;

import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketListener;
import org.metacorp.mindbug.exception.UnknownPlayerException;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.GameMode;
import org.metacorp.mindbug.service.GameService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class WsJoinEndpoint extends WebSocketApplication {

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
        int requiredPlayer = gameMode.getRequiredPlayer();

        joinQueues.computeIfAbsent(gameMode, gm -> new HashMap<>());
        
        for (CardSetName set : socket.getSets()) {
            if (set != null) {
                Map<CardSetName, Queue<JoinWebSocket>> modeQueues = joinQueues.get(gameMode);

                Queue<JoinWebSocket> setQueue = modeQueues.get(set);

                if (setQueue == null) {
                    joinQueues.get(gameMode).put(set, new LinkedList<>(Collections.singleton(socket)));
                } else {
                    if(setQueue.size() >= requiredPlayer - 1){
                        String playerId = socket.getPlayerId();

                        List<JoinWebSocket> otherPlayersSession = new ArrayList<>();

                        for(int i = 1; i < requiredPlayer; i++){
                            otherPlayersSession.add(setQueue.poll());
                        }

                        if (otherPlayersSession.stream().noneMatch(player -> playerId.equals(player.getPlayerId()) && player.getPlayerId() == null)) {
                            try {
                                Game game;
                                if(requiredPlayer == 2){
                                    game = gameService.createGame(UUID.fromString(playerId), UUID.fromString(otherPlayersSession.get(0).getPlayerId()), set);
                                } else if (requiredPlayer == 4){
                                    game = gameService.createGame(UUID.fromString(playerId), UUID.fromString(otherPlayersSession.get(0).getPlayerId()),
                                                        UUID.fromString(otherPlayersSession.get(1).getPlayerId()), UUID.fromString(otherPlayersSession.get(2).getPlayerId()), set);
                                } else {
                                    throw new IllegalStateException("Unsupported player count");
                                }
                                socket.send(game.getUuid().toString());
                                otherPlayersSession.forEach(player -> player.send(game.getUuid().toString()));
                            } catch (UnknownPlayerException e) {
                                // TODO Manage errors
                            }

                            //TODO Maybe implement a timeout system
                        }
                    } else {
                        setQueue.add(socket);
                    }
                }
            }
        }
    }

    @Override
    public void onMessage(WebSocket socket, String text) {
        if (text.equals("OK")) {

        }
        //TODO Manage players response
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

        System.out.println("Player " + socket.getPlayerName() + " (" + socket.getPlayerId() + ") left waiting queue");

        super.onClose(socket, frame);
    }
}
