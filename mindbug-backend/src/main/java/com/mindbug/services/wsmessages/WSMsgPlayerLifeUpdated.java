package com.mindbug.services.wsmessages;

import java.util.HashMap;

import com.mindbug.models.Game;
import com.mindbug.websocket.GameWSMessage;
import com.mindbug.websocket.WebsocketMessage;

public class WSMsgPlayerLifeUpdated extends WebsocketMessage {

    public WSMsgPlayerLifeUpdated(Long playerId, Game game) {
        super(GameWSMessage.PLAYER_LIFE_UPDATED.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("playerId", playerId);
        data.put("gameState", game);

        this.setData(data);

    }
    
}
