package com.mindbug.services;

import com.mindbug.models.Game;
import com.mindbug.models.Player;
import com.mindbug.utils.GameStatus;
import com.mindbug.utils.GameWSMessage;
import com.mindbug.websocket.WSMessageManager;
import com.mindbug.websocket.WebsocketMessage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.springframework.stereotype.Component;


@Component
public class GameSession {
    private Game game;

    private WSMessageManager gameWsMessageManager;

    private String wsChannel;

    private Long lastPlayerConfirmedJoin = null;

    private GameStatus status = GameStatus.NOT_STARTED;

    
    public GameSession(WSMessageManager gameWsMessageManager) {
        this.gameWsMessageManager = gameWsMessageManager;
    }

    public void initialize(Game game) {
        this.game = game;
        this.wsChannel = "/topic/game/" + game.getId();
        this.gameWsMessageManager.setChannel(wsChannel);
    }

    public void confirmJoin(Long playerId) {
        this.canConfirmJoin(playerId);

        if(this.lastPlayerConfirmedJoin == null) {
            // No player confirmed yet
            this.lastPlayerConfirmedJoin = playerId;
        } else {
            if(playerId != this.lastPlayerConfirmedJoin) {
                // The two players have confirmed. Send ws message newGame and update game status
                this.status = GameStatus.STARTED;
                this.gameWsMessageManager.sendMessage(GameWSMessage.NEW_GAME, this.game);
            } else {
                throw new IllegalArgumentException("Join already confirmed.");
            }
        }

    }

    public void canConfirmJoin(Long playerId) {
        if(this.status != GameStatus.NOT_STARTED) {
            // Cannot confrim join. Game already started.
            throw new IllegalStateException("Cannot confrim join. Game already started.");
        } 

        if(playerId != this.game.getPlayer1().getId() && playerId != this.game.getPlayer2().getId()) {
            // Cannot confrim join. Invalid player.
            throw new IllegalArgumentException("Cannot confrim join. Invalid player.");
        }
    }
    
}
