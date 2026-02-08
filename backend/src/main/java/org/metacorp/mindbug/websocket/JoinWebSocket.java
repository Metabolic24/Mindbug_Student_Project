package org.metacorp.mindbug.websocket;

import lombok.Getter;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.DefaultWebSocket;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.WebSocketListener;
import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.service.game.StartService;
import org.metacorp.mindbug.utils.WsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.metacorp.mindbug.utils.WsUtils.PLAYER_ID_KEY;
import static org.metacorp.mindbug.utils.WsUtils.PLAYER_NAME_KEY;
import static org.metacorp.mindbug.utils.WsUtils.SETS_KEY;

@Getter
public class JoinWebSocket extends DefaultWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(JoinWebSocket.class);

    private String playerId;
    private String playerName;
    private List<CardSetName> sets;

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

        List<String> setNames = WsUtils.getListFromQueryParam(SETS_KEY, this.servletRequest.getQueryString());
        if (setNames.isEmpty()) {
            throw new IllegalArgumentException("Missing required parameter 'sets'");
        } else {
            this.sets = setNames.stream().map(CardSetName::fromKey).collect(Collectors.toList());
        }

        LOGGER.info("Player {} ({}) joined waiting queue for sets {}", playerName, playerId, sets);

        super.onConnect();
    }

    @Override
    public void onMessage(String text) {
        // We ignore received messages
    }
}
