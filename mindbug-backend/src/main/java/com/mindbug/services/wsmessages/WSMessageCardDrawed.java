package com.mindbug.services.wsmessages;

import com.mindbug.models.Game;
import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import com.mindbug.websocket.WebsocketMessage;

import java.util.HashMap;

public class WSMessageCardDrawed extends WebsocketMessage {

    public WSMessageCardDrawed(Game game, Player player, GameSessionCard card) {
        super(GameWSMessage.CARD_DRAWED.getLabel());

        HashMap<String, Object> data = new HashMap<>();
        data.put("gameId", game.getId());
        data.put("playerId", player.getId());
        data.put("card", card);

        this.setData(data);
    }
}

