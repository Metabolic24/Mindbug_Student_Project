package com.mindbug.services.wsmessages;

import java.util.HashMap;

import com.mindbug.models.Game;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessageCardDestroyed  extends WebsocketMessage{

    public WSMessageCardDestroyed(Long playerId, Long gameSessionCardId, Game game) {
        super(GameWSMessage.CARD_DESTROYED.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("playerId", playerId);
        data.put("gameSessionCardId", gameSessionCardId);
        data.put("gameState", game);

        this.setData(data);
    }
    
}
