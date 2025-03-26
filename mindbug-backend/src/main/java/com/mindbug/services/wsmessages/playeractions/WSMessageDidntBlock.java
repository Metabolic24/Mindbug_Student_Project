package com.mindbug.services.wsmessages.playeractions;

import java.util.HashMap;

import com.mindbug.services.wsmessages.GameWSMessage;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessageDidntBlock extends WebsocketMessage {

    public WSMessageDidntBlock(Long gameId, Long playerId) {
        super(GameWSMessage.DIDNT_BLOCK.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("gameId", gameId);
        data.put("playerId", playerId);
    }
    
}
