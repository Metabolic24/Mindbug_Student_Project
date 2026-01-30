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
import org.metacorp.mindbug.service.GameService;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class WsJoinEndpoint extends WebSocketApplication {

    private final Map<CardSetName, Queue<JoinWebSocket>> joinQueues = new HashMap<>();

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

        for (CardSetName set : socket.getSets()) {
            if (set != null) {
                Queue<JoinWebSocket> setQueue = joinQueues.get(set);
                if (setQueue == null) {
                    joinQueues.put(set, new LinkedList<>(Collections.singleton(socket)));
                } else {
                    JoinWebSocket otherPlayerSession = setQueue.poll();

                    if (otherPlayerSession != null) {
                        String playerId = socket.getPlayerId();
                        if (!playerId.equals(otherPlayerSession.getPlayerId())) {
                            try {
                                Game game = gameService.createGame(UUID.fromString(playerId), UUID.fromString(otherPlayerSession.getPlayerId()), set);
                                socket.send(game.getUuid().toString());
                                otherPlayerSession.send(game.getUuid().toString());
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

        for (Queue<JoinWebSocket> joinQueue : joinQueues.values()) {
            List<JoinWebSocket> matchingSessions = joinQueue.stream()
                    .filter(currentSession -> socket.getPlayerId().equals(currentSession.getPlayerId()))
                    .toList();
            joinQueue.removeAll(matchingSessions);
        }

        System.out.println("Player " + socket.getPlayerName() + " (" + socket.getPlayerId() + ") left waiting queue");

        super.onClose(socket, frame);
    }
}
