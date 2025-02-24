package com.mindbug.services;

import com.mindbug.models.Game;
import com.mindbug.services.wsmessages.WSMessageNewGame;
import com.mindbug.websocket.WSMessageManager;

import org.springframework.context.annotation.Scope;
import lombok.Getter;

import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
@Getter
public class GameSession {
    private Game game;

    private WSMessageManager gameWsMessageManager;

    private String wsChannel;

    private Long lastPlayerConfirmedJoin = null;

    private GameStatus status = GameStatus.NOT_STARTED;

    private GameSessionValidation gameSessionValidation;

    
    public GameSession(Game game, WSMessageManager gameWsMessageManager, GameSessionValidation gameSessionValidation) {
        this.game = game;
        
        this.gameWsMessageManager = gameWsMessageManager;
        this.wsChannel = "/topic/game/" + game.getId();
        this.gameWsMessageManager.setChannel(wsChannel);

        this.gameSessionValidation = gameSessionValidation;
    }



    public void confirmJoin(Long playerId) {
        this.gameSessionValidation.canConfirmJoin(this, playerId);

        if (this.lastPlayerConfirmedJoin == null) {
            // No player confirmed yet
            this.lastPlayerConfirmedJoin = playerId;
        } else {
            if (playerId != this.lastPlayerConfirmedJoin) {
                // The two players have confirmed. Send ws message newGame and update game status
                this.status = GameStatus.STARTED;
                this.gameWsMessageManager.sendMessage(new WSMessageNewGame(this.game));
            } else {
                throw new IllegalArgumentException("Join already confirmed.");
            }
        }

    }


    public void attack(Long playerId, Long gameId, Long cardId) {

    }
    
}
