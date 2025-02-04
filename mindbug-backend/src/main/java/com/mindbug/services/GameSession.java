package com.mindbug.services;

import com.mindbug.models.Game;
import com.mindbug.utils.GameWSMessage;
import com.mindbug.websocket.WSMessageManager;
import com.mindbug.websocket.WebsocketMessage;

public class GameSession {
    private Game game;
    private WSMessageManager gameWsMessageManager;

    private String wsChannel;

    public GameSession(Game game) {
        this.game = game;

        this.wsChannel = "/topic/game/" + Long.toString(this.game.getId());
        this.gameWsMessageManager = new WSMessageManager(wsChannel);

        // Send newGame ws Message
        this.sendWebSocketMessage(GameWSMessage.NEW_GAME, game);
        
    }

    public void sendWebSocketMessage(String message, Object data) {
        WebsocketMessage wsMessage = new WebsocketMessage(message, data);
        this.gameWsMessageManager.sendMessage(wsMessage);
    }

    // TODO: save players and game state and everything need to be saved. 
    public void save() {

    }

    


}
