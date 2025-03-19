package com.mindbug.services.wsmessages;

import java.util.HashMap;

import com.mindbug.models.Game;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessagAskBlock extends WebsocketMessage {

    public WSMessagAskBlock(Long playerId, Game game) {
        super(GameWSMessage.ASK_BLOCK.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("playerId", playerId);
        data.put("gameId", game.getId());
        data.put("gameState", game);

        this.setData(data);
    }
    
}
