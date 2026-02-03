package org.metacorp.mindbug.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocketListener;
import org.metacorp.mindbug.dto.ws.WsGameEvent;
import org.metacorp.mindbug.utils.WsUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static org.metacorp.mindbug.utils.WsUtils.IS_AI_KEY;
import static org.metacorp.mindbug.utils.WsUtils.PLAYER_ID_KEY;

@Getter
public class GameWebSocket extends DefaultWebSocket {

    private UUID gameId;
    private UUID playerId;
    private boolean isAI;

    public GameWebSocket(ProtocolHandler protocolHandler, HttpRequestPacket request, WebSocketListener... listeners) {
        super(protocolHandler, request, listeners);
    }

    @Override
    public void onConnect() {
        String pathInfo = this.servletRequest.getPathInfo();
        if (pathInfo == null || !pathInfo.startsWith("/")) {
            throw new IllegalArgumentException("Missing required parameter 'gameId'");
        }

        this.gameId = UUID.fromString(pathInfo.substring(1));
        // Player ID is optional as game engine will send messages to this channel
        String playerQueryParam = WsUtils.getValueFromQueryParam(PLAYER_ID_KEY, this.servletRequest.getQueryString());
        if (playerQueryParam != null) {
            playerId = UUID.fromString(playerQueryParam);
        }

        // AI field is optional and only filled if this WS is linked to an AI player
        String aiQueryParam = WsUtils.getValueFromQueryParam(IS_AI_KEY, this.servletRequest.getQueryString());
        if (aiQueryParam != null) {
            isAI = Boolean.parseBoolean(aiQueryParam);
        }

        // TODO Message de DEBUG (à améliorer)
        // System.out.println("--- Connected to Websocket " + this.servletRequest.getRequestURI());

        super.onConnect();
    }
}
