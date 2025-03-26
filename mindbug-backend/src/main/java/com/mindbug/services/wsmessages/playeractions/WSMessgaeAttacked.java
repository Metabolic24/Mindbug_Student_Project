package com.mindbug.services.wsmessages.playeractions;

import java.util.HashMap;

import com.mindbug.services.wsmessages.GameWSMessage;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessgaeAttacked extends WebsocketMessage {

    public WSMessgaeAttacked(Long gameId, Long playerId, Long gameSessionCardId) {
        super(GameWSMessage.DIDNT_BLOCK.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("gameId", gameId);
        data.put("playerId", playerId);
        data.put("gameSessionCardId", gameSessionCardId);

    }
    
}
