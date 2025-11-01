package org.metacorp.mindbug.websocket;

import lombok.Getter;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocketListener;
import org.metacorp.mindbug.utils.WsUtils;

import static org.metacorp.mindbug.utils.WsUtils.PLAYER_ID_KEY;
import static org.metacorp.mindbug.utils.WsUtils.PLAYER_NAME_KEY;

@Getter
public class JoinWebSocket extends DefaultWebSocket {

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
