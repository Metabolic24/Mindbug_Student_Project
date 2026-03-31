package org.metacorp.mindbug.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.DeploymentException;
import org.metacorp.mindbug.dto.ws.WsGameEvent;
import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.mapper.GameStateMapper;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.WsUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;

/**
 * Service to initialize and send event to a GameWebSocket
 */
public class WebSocketService {

    private static final String GAME_WS_URI = "ws://localhost:8080/ws/game/";

    /**
     * Initialize the connection to the GameWebSocket
     *
     * @param game the current game
     */
    public static void initGameChannel(Game game) {
        try {
            // Create a WebSocket so the game is initialized
            GameWebSocketClient client = new GameWebSocketClient(GAME_WS_URI + game.getUuid());
            game.setWsClient(client);
            game.setWebSocketUp(true);

            // Create a WebSocket for each AI player
            for (Player player : game.getPlayers()) {
                if (player.isAI()) {
                    GameWebSocketClient aiSocketClient = new GameWebSocketClient(GAME_WS_URI + game.getUuid() + "?"
                            + WsUtils.PLAYER_ID_KEY + "=" + player.getUuid() + "&" + WsUtils.IS_AI_KEY + "=true");
                    aiSocketClient.close();
                }
            }
        } catch (IOException | URISyntaxException | DeploymentException e) {
            game.getLogger().warn("Unable to join WS : WS communication is disabled", e);
            game.setWebSocketUp(false);
        }
    }

    /**
     * Send an event to the GameWebSocket related to the current game
     *
     * @param eventType the event type
     * @param game      the current game
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void sendGameEvent(WsGameEventType eventType, Game game) throws WebSocketException {
        if (game.isWebSocketUp()) {
            boolean eventSent = false;
            int retriesCount = 0;

            while (!eventSent) {
                try {
                    GameWebSocketClient client = game.getWsClient();
                    if (client == null || !client.isConnected()) {
                        client = new GameWebSocketClient(GAME_WS_URI + game.getUuid());
                        game.setWsClient(client);
                    }

                    WsGameEvent event = new WsGameEvent(eventType, GameStateMapper.fromGame(game));
                    client.sendMessage(new ObjectMapper().writeValueAsString(event));
                    eventSent = true;
                } catch (IOException | URISyntaxException | DeploymentException e) {
                    if (retriesCount == 3) {
                        String errorMessage = MessageFormat.format("Error while sending game event {0}", eventType.name());
                        throw new WebSocketException(errorMessage, e);
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            game.getLogger().error("Unexpected error while waiting for WS event retry", ie);
                        }

                        retriesCount++;
                        System.err.println("Retrying sending game event " + eventType.name() + " (" + retriesCount + "/3)");
                    }
                }
            }
        }
    }
}
