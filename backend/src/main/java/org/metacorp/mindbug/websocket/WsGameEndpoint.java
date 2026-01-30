package org.metacorp.mindbug.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.glassfish.grizzly.websockets.WebSocketListener;
import org.metacorp.mindbug.dto.GameStateDTO;
import org.metacorp.mindbug.dto.ws.WsGameEvent;
import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.dto.ws.WsPlayerGameEvent;
import org.metacorp.mindbug.dto.ws.WsPlayerGameState;
import org.metacorp.mindbug.mapper.GameStateMapper;
import org.metacorp.mindbug.service.GameService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WsGameEndpoint extends WebSocketApplication {

    private final Map<UUID, List<GameWebSocket>> sessions = new HashMap<>();

    private final GameService gameService;

    /**
     * Constructor
     *
     * @param gameService the Game service
     */
    public WsGameEndpoint(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket requestPacket, WebSocketListener... listeners) {
        return new GameWebSocket(handler, requestPacket, listeners);
    }

    @Override
    public void onConnect(WebSocket rawSocket) {
        super.onConnect(rawSocket);

        GameWebSocket socket = (GameWebSocket) rawSocket;
        UUID gameId = socket.getGameId();
        UUID playerId = socket.getPlayerId();

        if (playerId == null && !sessions.containsKey(gameId)) {
            sessions.put(gameId, new ArrayList<>());
            System.out.println("Websocket created for game " + gameId);
        } else if (playerId != null && sessions.containsKey(gameId)) {
            sessions.get(gameId).add(socket);
            System.out.println("Player " + playerId + " joined game " + gameId);

            GameStateDTO gameStateDTO = GameStateMapper.fromGame(gameService.findById(gameId));
            WsPlayerGameEvent playerGameEvent = new WsPlayerGameEvent(WsGameEventType.STATE);
            playerGameEvent.setState(new WsPlayerGameState(gameStateDTO, playerId.equals(gameStateDTO.getPlayer().getUuid())));
            try {
                String gameStateData = new ObjectMapper().writeValueAsString(playerGameEvent);
                System.out.println("--- Message for player " + playerId + " : " + gameStateData);
                socket.send(gameStateData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onMessage(WebSocket rawSocket, String message) {
        GameWebSocket socket = (GameWebSocket) rawSocket;

        try {
            ObjectMapper mapper = new ObjectMapper();
            WsGameEvent gameEvent = mapper.readValue(message, new TypeReference<>() {
            });

            System.out.println("--- Message: " + gameEvent);

            UUID gameId = socket.getGameId();
            if (sessions.containsKey(gameId)) {
                GameStateDTO gameState = gameEvent.getState();

                for (GameWebSocket playerSocket : sessions.get(gameId)) {
                    WsPlayerGameEvent playerGameEvent = new WsPlayerGameEvent(gameEvent.getType());
                    String eventData;

                    try {
                        UUID playerId = playerSocket.getPlayerId();
                        if (playerId.equals(gameState.getPlayer().getUuid())) {
                            playerGameEvent.setState(new WsPlayerGameState(gameState, true));
                            eventData = mapper.writeValueAsString(playerGameEvent);
                        } else if (playerId.equals(gameState.getOpponent().getUuid())) {
                            playerGameEvent.setState(new WsPlayerGameState(gameState, false));
                            eventData = mapper.writeValueAsString(playerGameEvent);
                        } else {
                            // Should not happen
                            eventData = message;
                        }

                        playerSocket.send(eventData);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            //TODO Manage errors
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(WebSocket rawSocket, DataFrame frame) {
        GameWebSocket socket = (GameWebSocket) rawSocket;
        UUID gameId = socket.getGameId();
        UUID playerId = socket.getPlayerId();

        if (sessions.containsKey(gameId) && socket.getPlayerId() != null) {
            sessions.get(gameId).remove(socket);
            System.out.println("Player " + socket.getPlayerId() + " left game " + gameId);

            gameService.endGame(playerId, gameId);
        }

        super.onClose(socket, frame);
    }
}
