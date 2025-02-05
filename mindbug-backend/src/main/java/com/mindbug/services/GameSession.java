package com.mindbug.services;

import com.mindbug.models.Game;
import com.mindbug.utils.GameWSMessage;
import com.mindbug.websocket.WSMessageManager;
import com.mindbug.websocket.WebsocketMessage;

import org.springframework.stereotype.Component;


@Component
public class GameSession {
    private Game game;

    private WSMessageManager gameWsMessageManager;

    private String wsChannel;

    
    public GameSession(WSMessageManager gameWsMessageManager) {
        this.gameWsMessageManager = gameWsMessageManager;
    }

    public void initialize(Game game) {
        this.game = game;
        this.wsChannel = "/topic/game/" + game.getId();
        this.gameWsMessageManager.setChannel(wsChannel);
        this.sendWebSocketMessage(GameWSMessage.NEW_GAME, game);
    }

    public void sendWebSocketMessage(String message, Object data) {
        WebsocketMessage wsMessage = new WebsocketMessage(message, data);
        this.gameWsMessageManager.sendMessage(wsMessage);
    }


    


}
