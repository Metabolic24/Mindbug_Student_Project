package com.mindbug.services.gamecore;

import com.mindbug.models.Game;
import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import com.mindbug.services.PlayerService;
import com.mindbug.services.wsmessages.WSMessageNewGame;
import com.mindbug.services.wsmessages.WSMessageNewTurn;
import com.mindbug.websocket.WSMessageManager;

import org.springframework.context.ApplicationContext;
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

    private GameSessionValidation gameSessionValidation;

    private Battle battle;

    private  ApplicationContext applicationContext;

    private PlayerService playerService;

    
    public GameSession(Game game, WSMessageManager gameWsMessageManager, GameSessionValidation gameSessionValidation,
    ApplicationContext applicationContext, PlayerService playerService) {
        this.game = game;
        
        this.gameWsMessageManager = gameWsMessageManager;
        this.wsChannel = "/topic/game/" + game.getId();
        this.gameWsMessageManager.setChannel(wsChannel);

        this.gameSessionValidation = gameSessionValidation;

        this.applicationContext = applicationContext;
        this.playerService = playerService;

        this.battle = this.applicationContext.getBean(Battle.class);
    }



    public void confirmJoin(Long playerId) {
        this.gameSessionValidation.canConfirmJoin(this, playerId);

        if (this.lastPlayerConfirmedJoin == null) {
            // No player confirmed yet
            this.lastPlayerConfirmedJoin = playerId;
        } else {
            if (playerId != this.lastPlayerConfirmedJoin) {
                // The two players have confirmed. Send ws message newGame and update game status
                this.gameWsMessageManager.sendMessage(new WSMessageNewGame(this.game));

                // Start a turn
                newTurn();
            } else {
                throw new IllegalArgumentException("Join already confirmed.");
            }
        }

    }

    public void newTurn() {
        // For now we assume 1st player is player1
        this.game.setCurrentPlayer(game.getPlayer1());

        // Send WS message of ne turn
        this.gameWsMessageManager.sendMessage(new WSMessageNewTurn(game));
    }


    public void attack(Long playerId, Long gameId, Long sessionCardId) {
        // TODO: Set game status to play_or_attack in new tour to make it work
        this.gameSessionValidation.canAttack(this, playerId, sessionCardId);

        Player player = getPlayer(playerId);
        GameSessionCard sessionCard = playerService.getHandCard(player, sessionCardId);


        this.battle.attack(player, sessionCard);
    }
    

    public Player getPlayer(Long playerId) {
        this.gameSessionValidation.validPlayer(this, playerId);
        
        if (playerId == this.game.getPlayer1().getId())
            return this.game.getPlayer1();
        else 
            return this.game.getPlayer2();
    }
}
