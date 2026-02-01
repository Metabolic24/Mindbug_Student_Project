package org.metacorp.mindbug.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.ws.WebSocket;
import com.ning.http.client.ws.WebSocketUpgradeHandler;
import org.metacorp.mindbug.dto.ws.WsGameEvent;
import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.mapper.GameStateMapper;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.AiPlayer;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.WsUtils;
import org.metacorp.mindbug.websocket.GameWebSocket;

import java.util.concurrent.ExecutionException;

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
        // Init WS client
        try (AsyncHttpClient wsClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build())) {
            // Create a WebSocket so the game is initialized
            wsClient.prepareGet(GAME_WS_URI + game.getUuid()).execute(new WebSocketUpgradeHandler.Builder().build()).get();
            game.setWebSocketUp(true);

            // Create a WebSocket for each AI player
            for (Player player : game.getPlayers()) {
                if (player.isAI()) {
                    wsClient.prepareGet(GAME_WS_URI + game.getUuid() + "?"
                                    + WsUtils.PLAYER_ID_KEY + "=" + player.getUuid() + "&" + WsUtils.IS_AI_KEY + "=true")
                            .execute(new WebSocketUpgradeHandler.Builder().build()).get();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Unable to join WS : WS communication is disabled");
            game.setWebSocketUp(false);
        }
    }

    /**
     * Send an event to the GameWebSocket related to the current game
     *
     * @param eventType the event type
     * @param game      the current game
     */
    public static void sendGameEvent(WsGameEventType eventType, Game game) {
        if (game.isWebSocketUp()) {
            try (AsyncHttpClient c = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().build())) {
                WebSocket socket = c.prepareGet(GAME_WS_URI + game.getUuid()).execute(new WebSocketUpgradeHandler.Builder().build()).get();

                WsGameEvent event = new WsGameEvent(eventType, GameStateMapper.fromGame(game));
                socket.sendMessage(new ObjectMapper().writeValueAsString(event));
            } catch (ExecutionException | InterruptedException | JsonProcessingException e) {
                // TODO Manage errors
                throw new RuntimeException(e);
            }
        }
    }
}
