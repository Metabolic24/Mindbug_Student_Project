package com.mindbug.services.wsmessages.playeractions;

import java.util.HashMap;

import com.mindbug.websocket.GameWSMessage;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessageAttacked extends WebsocketMessage {

    public WSMessageAttacked(Long gameId, Long playerId, Long gameSessionCardId) {
        super(GameWSMessage.ATTACKED.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("gameId", gameId);
        data.put("playerId", playerId);
        data.put("cardId", gameSessionCardId);

        this.setData(data);

    }
    
}
