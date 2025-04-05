package com.mindbug.services.wsmessages;

import java.util.HashMap;

import com.mindbug.websocket.GameWSMessage;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessageMatchFound extends WebsocketMessage {

    public WSMessageMatchFound(Long gameId, Long playerId) {
        super(GameWSMessage.MATCH_FOUND.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("gameId", gameId);
        data.put("playerId", playerId);

        this.setData(data);
        
    }
}
