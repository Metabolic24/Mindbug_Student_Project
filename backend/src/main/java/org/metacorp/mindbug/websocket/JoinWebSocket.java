package org.metacorp.mindbug.websocket;

import lombok.Getter;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import org.metacorp.mindbug.utils.WsUtils;

@Getter
public class JoinWebSocket extends DefaultWebSocket {

    private static final String PLAYER_ID_KEY = "playerId";
    private static final String PLAYER_NAME_KEY = "playerName";

    private String playerId;
    private String playerName;

    public JoinWebSocket(ProtocolHandler protocolHandler, HttpRequestPacket request, WebSocketListener... listeners) {
        super(protocolHandler, request, listeners);
    }

    @Override
    public void onConnect() {
        this.playerId = WsUtils.getValueFromQueryParam(PLAYER_ID_KEY, this.servletRequest.getQueryString());
        if (playerId == null) {
            throw new IllegalArgumentException("Missing required parameter 'playerId'");
        }

        this.playerName = WsUtils.getValueFromQueryParam(PLAYER_NAME_KEY, this.servletRequest.getQueryString());
        if (playerName == null) {
            throw new IllegalArgumentException("Missing required parameter 'playerName'");
        }

        System.out.println("Player " + playerName + " (" + playerId + ") joined waiting queue");

        super.onConnect();
    }

    @Override
    public void onMessage(String text) {
        // We ignore received messages
    }
}
