package com.mindbug.services.gamecore;

import com.mindbug.models.Game;
import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import com.mindbug.services.CardService;
import com.mindbug.services.GameServer;
import com.mindbug.services.PlayerService;
import com.mindbug.services.wsmessages.WSMessagAskBlock;
import com.mindbug.services.wsmessages.WSMessageCardDestroyed;
import com.mindbug.services.wsmessages.WSMessageGameOver;
import com.mindbug.services.wsmessages.WSMessageNewGame;
import com.mindbug.services.wsmessages.WSMessageNewTurn;
import com.mindbug.services.wsmessages.WSMsgPlayerLifeUpdated;
import com.mindbug.services.wsmessages.playeractions.WSMessageBlocked;
import com.mindbug.services.wsmessages.playeractions.WSMessageDidntBlock;
import com.mindbug.services.wsmessages.playeractions.WSMessgaeAttacked;
import com.mindbug.websocket.WSMessageManager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    private List<Long> confirmJoinPlayers;

    private GameSessionValidation gameSessionValidation;

    private Battle battle;

    private  ApplicationContext applicationContext;

    private PlayerService playerService;

    @Autowired
    private CardService cardService;

    @Autowired
    private GameServer gameServer;

    public GameSession(Game game, WSMessageManager gameWsMessageManager, GameSessionValidation gameSessionValidation,
    ApplicationContext applicationContext, PlayerService playerService) {
        this.game = game;

        this.confirmJoinPlayers = new ArrayList<>();

        this.gameWsMessageManager = gameWsMessageManager;
        this.wsChannel = "/topic/game/" + game.getId();
        this.gameWsMessageManager.setChannel(wsChannel);

        this.gameSessionValidation = gameSessionValidation;

        this.applicationContext = applicationContext;
        this.playerService = playerService;
    }

    public void confirmJoin(Long playerId) {
        this.gameSessionValidation.canConfirmJoin(this, playerId);

        if (this.confirmJoinPlayers.contains(playerId)) {
            throw new IllegalArgumentException("Join already confirmed.");
        } else {
            this.confirmJoinPlayers.add(playerId);
        }

        if (this.confirmJoinPlayers.size() == 2) {
            cardService.distributeCards(game);
            // The two players have confirmed. Send ws message newGame and update game status
            this.gameWsMessageManager.sendMessage(new WSMessageNewGame(this.game));

            // Start a turn
            newTurn();
        }
    }

    public void newTurn() {
        checkPlayerCanDoAction(this.game.getCurrentPlayer());

        if(this.game.isGameOver()) {
            return;
        }

        if (this.game.getCurrentPlayer() == null) {
            // For now we assume 1st player is player1
            this.game.setCurrentPlayer(game.getPlayer1());
        } else {
            this.game.setCurrentPlayer(getOpponent());
        }
        

        // Send WS message of ne turn
        this.gameWsMessageManager.sendMessage(new WSMessageNewTurn(game));

    }

    public void attack(Long playerId, Long sessionCardId) {
        this.gameSessionValidation.canAttack(this, playerId, sessionCardId);

        Player player = getPlayer(playerId);
        GameSessionCard sessionCard = playerService.getBattlefiedCard(player, sessionCardId);

        this.battle = this.applicationContext.getBean(Battle.class);

        this.battle.attack(this, player, sessionCard);
    }

    public void dontBlock(Long playerId) {
        this.gameSessionValidation.canDoDontBlock(this, playerId);


        Player player = getPlayer(playerId);

        this.battle.dontBlock(this, player);

    }

    public void block(Long playerId, Long sessionCardId) {
        this.gameSessionValidation.canBlock(this, playerId, sessionCardId);

        Player player = getPlayer(playerId);
        GameSessionCard sessionCard = playerService.getBattlefiedCard(player, sessionCardId);

        this.battle.block(this, player, sessionCard);

    }

    public void directAttack(Player opponent) {
        this.battle.directAttack(this, opponent);

        // Reset after battle end
        this.battle = null;

        if (isGameOverPlayerLife(opponent)) {
            this.endGame();
        } else {
            this.newTurn();
        }

    }

    public void resolveBattle() {
        this.battle.resolveBattle(this);

        // Reset after battle end
        this.battle = null;
        
        this.newTurn();
    }

    public void playCard(Long playerId, Long sessionCardId) {

        this.gameSessionValidation.canPlayCard(this, playerId, sessionCardId);

        Player player = this.getPlayer(playerId);
        GameSessionCard sessionCard = playerService.getHandCard(player, sessionCardId);

        player.getHand().remove(sessionCard);

        player.getBattlefield().add(sessionCard);

        // TODO: sent webscoket

        this.newTurn();
    }

    public Player getPlayer(Long playerId) {
        this.gameSessionValidation.validPlayer(this, playerId);

        if (playerId.equals(this.game.getPlayer1().getId())) {
            return this.game.getPlayer1();
        } else {
            return this.game.getPlayer2();
        }
    }

    public Player getOpponent() {
        if (isCurrentPlayer(game.getPlayer1().getId())) {
            return game.getPlayer2();
        } else {
            return game.getPlayer1();
        }
    }

    public boolean isCurrentPlayer(Long playerId) {
        return game.getCurrentPlayer() != null && playerId == game.getCurrentPlayer().getId();
    }

    public List<GameSessionCard> getPlayerHand(Long playerId) {
        Player player = getPlayer(playerId);
        return player.getHand();
    }
    
    public void destroyCard(GameSessionCard destroyedCard, Player player) {
        // Send card to discarPile
        player.getBattlefield().remove(destroyedCard);
        player.getDiscardPile().add(destroyedCard);

        // Send card destroyed websocket message
        sendWSMsgCardDestroyed(player.getId(), destroyedCard.getId());
    }

    public boolean isGameOverPlayerLife(Player player) {
        if (player.getLifepoints() < 1) {
            this.game.setGameOver(true);
        }

        return this.game.isGameOver();
    }

    public boolean checkPlayerCanDoAction(Player player) {
        
        if(player.getHand().size() < 1 || player.getBattlefield().size() < 1) {
            this.game.setGameOver(true);
        }

        return this.game.isGameOver();
    }

    public void endGame() {
        this.sendWSMsgGameOver();
        this.gameServer.removeGameSession(this.game.getId());
    }


    public void sendWSMsgAskBlock(Long playerId) {
        this.gameWsMessageManager.sendMessage(new WSMessagAskBlock(playerId, this.game));
    }

    public void sendWSMsgAttacked(Long playerId, Long gameSessionCardId) {
        this.gameWsMessageManager.sendMessage(new WSMessgaeAttacked(this.game.getId(), playerId, gameSessionCardId));
    }

    public void sendWSMsgBlocked(Long playerId, Long gameSessionCardId) {
        this.gameWsMessageManager.sendMessage(new WSMessageBlocked(this.game.getId(), playerId, gameSessionCardId));
    }

    public void sendWSMsgDidntBlocked(Long playerId) {
        this.gameWsMessageManager.sendMessage(new WSMessageDidntBlock(this.game.getId(), playerId));
    }

    public void sendWSMsgPlayerLifeUpdated(Long playerId) {
        this.gameWsMessageManager.sendMessage(new WSMsgPlayerLifeUpdated(playerId, this.game));
    }

    public void sendWSMsgCardDestroyed(Long playerId, Long gameSessionCardId) {
        this.gameWsMessageManager.sendMessage(new WSMessageCardDestroyed(playerId, gameSessionCardId, this.game));
    }

    public void sendWSMsgGameOver() {
        this.gameWsMessageManager.sendMessage(new WSMessageGameOver(this.game));
    }

}
