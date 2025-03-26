package com.mindbug.services.wsmessages;

import java.util.HashMap;

import com.mindbug.models.Game;
import com.mindbug.websocket.WebsocketMessage;

public class WSMessageNewTurn extends WebsocketMessage  {

    public WSMessageNewTurn(Game game) {
        super(GameWSMessage.NEW_TURN.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("currentPlayer", game.getCurrentPlayer().getId());
        data.put("gameId", game.getId());
        data.put("gameState", game);

        this.setData(data);
    }
}
