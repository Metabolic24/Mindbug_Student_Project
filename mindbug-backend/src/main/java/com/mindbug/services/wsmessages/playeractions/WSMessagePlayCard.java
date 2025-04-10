package com.mindbug.services.wsmessages.playeractions;

import com.mindbug.models.Game;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessagePlayCard extends WebsocketMessage {
    public WSMessagePlayCard(Game game) {
        super("gameState");
        this.setData(game);
    }
}
