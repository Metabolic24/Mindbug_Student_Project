package com.mindbug.services.wsmessages;

import java.util.HashMap;

import com.mindbug.models.Game;
import com.mindbug.websocket.GameWSMessage;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessageGameOver extends WebsocketMessage {

    public WSMessageGameOver(Game game) {
        super(GameWSMessage.GAME_OVER.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("gameState", game);

        this.setData(data);
    }
    
}
