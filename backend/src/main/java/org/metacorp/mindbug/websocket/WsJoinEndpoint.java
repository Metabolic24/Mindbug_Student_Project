package org.metacorp.mindbug.websocket;

import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.service.GameService;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class WsJoinEndpoint extends WebSocketApplication {

    private static final Queue<JoinWebSocket> sessions = new LinkedList<>();

    // TODO Create a map or a structure to store player responses and start the game when both players have confirmed

    private final GameService gameService;

    /**
     * Constructor
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
        JoinWebSocket otherPlayerSession = sessions.poll();

        if (otherPlayerSession != null) {
            String playerId = socket.getPlayerId();
            if (!playerId.equals(otherPlayerSession.getPlayerId())) {
                Game game = gameService.createGame(
                        UUID.fromString(playerId), socket.getPlayerName(),
                        UUID.fromString(otherPlayerSession.getPlayerId()), otherPlayerSession.getPlayerName());

                socket.send(game.getUuid().toString());
                otherPlayerSession.send(game.getUuid().toString());

                //TODO Maybe implement a timeout system
            }
        } else {
            sessions.add(socket);
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

        List<JoinWebSocket> matchingSessions = sessions.stream()
                .filter(currentSession -> socket.getPlayerId().equals(currentSession.getPlayerId()))
                .toList();
        sessions.removeAll(matchingSessions);

        System.out.println("Player " + socket.getPlayerName() + " (" + socket.getPlayerId() + ") left waiting queue");

        super.onClose(socket, frame);
    }
}
