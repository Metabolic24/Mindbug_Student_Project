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
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.mapper.GameStateMapper;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.utils.AiUtils;
import org.slf4j.Logger;

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
        boolean isAi = socket.isAI();

        Game game = gameService.findById(gameId);
        Logger logger = game.getLogger();

        if (playerId == null && !sessions.containsKey(gameId)) {
            sessions.put(gameId, new ArrayList<>());
            // We cannot log into the game logger as it is probably not already available
            logger.info("Websocket initialized");
        } else if (playerId != null && sessions.containsKey(gameId)) {
            sessions.get(gameId).add(socket);
            logger.info("Player {} joined the game", playerId);

            if (!isAi) {
                GameStateDTO gameStateDTO = GameStateMapper.fromGame(game);
                WsPlayerGameEvent playerGameEvent = new WsPlayerGameEvent(WsGameEventType.STATE);
                playerGameEvent.setState(new WsPlayerGameState(gameStateDTO, playerId.equals(gameStateDTO.getPlayer().getUuid())));
                try {
                    String gameStateData = new ObjectMapper().writeValueAsString(playerGameEvent);
                    logger.debug("Sending START game state to player {}", playerId);
                    socket.send(gameStateData);
                } catch (JsonProcessingException e) {
                    // Should not happen
                    logger.error("Failed to send START game state to player {}", playerId, e);
                    throw new RuntimeException(e);
                }

                if (game.getCurrentPlayer().isAI()) {
                    AiUtils.processGameEvent(game.getCurrentPlayer().getUuid(), new WsGameEvent(WsGameEventType.STATE, gameStateDTO), gameService);
                }
            }
        }
    }

    @Override
    public void onMessage(WebSocket rawSocket, String message) {
        GameWebSocket socket = (GameWebSocket) rawSocket;
        UUID gameId = socket.getGameId();

        Game game = gameService.findById(gameId);
        Logger logger = game.getLogger();

        try {
            ObjectMapper mapper = new ObjectMapper();
            WsGameEvent gameEvent = mapper.readValue(message, new TypeReference<>() {
            });

            logger.debug("Game event received : {}", gameEvent.getType());

            if (sessions.containsKey(gameId)) {
                List<GameWebSocket> iaWebSockets = new ArrayList<>();

                for (GameWebSocket playerSocket : sessions.get(gameId)) {
                    if (playerSocket.isAI()) {
                        if (shouldAiPlayerShouldPlay(playerSocket, gameEvent)) {
                            iaWebSockets.add(playerSocket);
                        }
                    } else {
                        sendMessageToRealPlayer(playerSocket, gameEvent, mapper);
                    }
                }

                for (GameWebSocket playerSocket : iaWebSockets) {
                    AiUtils.processGameEvent(playerSocket.getPlayerId(), gameEvent, gameService);
                }
            }
        } catch (JsonProcessingException e) {
            // Should not happen
            logger.warn("Unable to serialize/deserialize game event", e);
        }
    }

    private boolean shouldAiPlayerShouldPlay(GameWebSocket playerSocket, WsGameEvent gameEvent) {
        UUID playerId = playerSocket.getPlayerId();

        return switch (gameEvent.getType()) {
            case NEW_TURN, STATE -> gameEvent.getState().getPlayer().getUuid().equals(playerId);
            case CARD_PICKED -> gameEvent.getState().getOpponent().getUuid().equals(playerId)
                    && gameEvent.getState().getOpponent().getMindbugCount() > 0;
            case CHOICE -> gameEvent.getState().getChoice().getPlayerToChoose().equals(playerId);
            case WAITING_ATTACK_RESOLUTION -> gameEvent.getState().getOpponent().getUuid().equals(playerId);
            case FINISHED -> {
                playerSocket.close();
                yield false;
            }
            default -> false;
        };
    }

    private void sendMessageToRealPlayer(GameWebSocket playerSocket, WsGameEvent gameEvent, ObjectMapper mapper) throws JsonProcessingException {
        WsPlayerGameEvent playerGameEvent = new WsPlayerGameEvent(gameEvent.getType());
        GameStateDTO gameState = gameEvent.getState();
        String eventData;

        UUID playerId = playerSocket.getPlayerId();
        if (playerId.equals(gameState.getPlayer().getUuid())) {
            playerGameEvent.setState(new WsPlayerGameState(gameState, true));
            eventData = mapper.writeValueAsString(playerGameEvent);
        } else if (playerId.equals(gameState.getOpponent().getUuid())) {
            playerGameEvent.setState(new WsPlayerGameState(gameState, false));
            eventData = mapper.writeValueAsString(playerGameEvent);
        } else {
            // Should not happen
            eventData = "Unexpected data";
        }

        playerSocket.send(eventData);
    }

    @Override
    public void onClose(WebSocket rawSocket, DataFrame frame) {
        GameWebSocket socket = (GameWebSocket) rawSocket;
        UUID gameId = socket.getGameId();
        UUID playerId = socket.getPlayerId();

        Game game = gameService.findById(gameId);
        Logger logger = game.getLogger();

        if (playerId != null) {
            List<GameWebSocket> sessionSockets = sessions.get(gameId);
            if (sessionSockets != null) {
                if (!socket.isAI()) {
                    sessionSockets.remove(socket);
                    logger.info("Player {} left the game", playerId);
                    try {
                        gameService.endGame(playerId, gameId);
                    } catch (WebSocketException e) {
                        logger.warn("An error occurred while trying to end game", e);
                    }
                }

                if (sessionSockets.isEmpty()) {
                    sessions.remove(gameId);
                }
            }
        }

        super.onClose(socket, frame);
    }
}
