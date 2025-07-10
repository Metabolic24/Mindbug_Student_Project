package org.metacorp.mindbug.websocket;

import lombok.Getter;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import org.metacorp.mindbug.utils.WsUtils;

import java.util.UUID;

@Getter
public class GameWebSocket extends DefaultWebSocket {

    private static final String PLAYER_ID_KEY = "playerId";

    private UUID gameId;
    private UUID playerId;

    public GameWebSocket(ProtocolHandler protocolHandler, HttpRequestPacket request, WebSocketListener... listeners) {
        super(protocolHandler, request, listeners);
    }

    @Override
    public void onConnect() {
        String pathInfo = this.servletRequest.getPathInfo();
        if (pathInfo == null ||!pathInfo.startsWith("/")) {
            throw new IllegalArgumentException("Missing required parameter 'gameId'");
        }

        this.gameId = UUID.fromString(pathInfo.substring(1));
        // Player ID is optional as game engine will send messages to this channel
        String playerQueryParam = WsUtils.getValueFromQueryParam(PLAYER_ID_KEY, this.servletRequest.getQueryString());
        if (playerQueryParam != null) {
            playerId = UUID.fromString(playerQueryParam);
        }

        System.out.println ("--- Connected to Websocket " + this.servletRequest.getRequestURI());

        super.onConnect();
    }
}
