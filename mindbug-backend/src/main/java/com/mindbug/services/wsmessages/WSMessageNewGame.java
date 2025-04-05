package com.mindbug.services.wsmessages;

import com.mindbug.models.Game;
import com.mindbug.websocket.GameWSMessage;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessageNewGame extends WebsocketMessage {

    public WSMessageNewGame(Game game) {
        super(GameWSMessage.NEW_GAME.getLabel(), game);
    }
}
