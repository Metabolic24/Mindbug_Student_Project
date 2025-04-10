package com.mindbug.services.wsmessages.playeractions;

import com.mindbug.models.Game;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessageGameState extends WebsocketMessage {
    public WSMessageGameState(Game game) {
        super("gameState");
        this.setData(game);
    }
}
