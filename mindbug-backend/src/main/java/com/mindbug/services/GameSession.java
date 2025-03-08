package com.mindbug.services;

import com.mindbug.models.Card;
import com.mindbug.models.Game;
import com.mindbug.models.Player;
import com.mindbug.services.wsmessages.WSMessageNewGame;
import com.mindbug.websocket.WSMessageManager;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;


@Component
@Scope("prototype")
public class GameSession {
    private Game game;

    private WSMessageManager gameWsMessageManager;

    private String wsChannel;

    private Long lastPlayerConfirmedJoin = null;

    private GameStatus status = GameStatus.NOT_STARTED;

    
    public GameSession(Game game, WSMessageManager gameWsMessageManager) {
        this.game = game;
        
        this.gameWsMessageManager = gameWsMessageManager;
        this.wsChannel = "/topic/game/" + game.getId();
        this.gameWsMessageManager.setChannel(wsChannel);
    }

    public void confirmJoin(Long playerId) {
        this.canConfirmJoin(playerId);

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

    public void canConfirmJoin(Long playerId) {
        if (this.status != GameStatus.NOT_STARTED) {
            // Cannot confrim join. Game already started.
            throw new IllegalStateException("Cannot confrim join. Game already started.");
        } 

        if (playerId != this.game.getPlayer1().getId() && playerId != this.game.getPlayer2().getId()) {
            // Cannot confrim join. Invalid player.
            throw new IllegalArgumentException("Cannot confrim join. Invalid player.");
        }
    }

    public void initializeGame() {
        try {
            CardService cardService = new CardService();
            List<Card> rawCards = cardService.getCardsBySet("First_Contact");
            List<Card> cards = expandCardCopies(rawCards);
            Collections.shuffle(cards);

            if (cards.size() < 20) {
                throw new IllegalStateException("Not enough cards to start the game!");
            }

            List<Card> player1Cards = new ArrayList<>(cards.subList(0, 10));
            List<Card> player2Cards = new ArrayList<>(cards.subList(10, 20));

            distributeCardsToPlayer(this.game.getPlayer1(), player1Cards);
            distributeCardsToPlayer(this.game.getPlayer2(), player2Cards);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Card> expandCardCopies(List<Card> rawCards) {
        List<Card> expandedCards = new ArrayList<>();
        for (Card card : rawCards) {
            for (int i = 0; i < card.getCopies(); i++) {
                expandedCards.add(new Card(card));
            }
        }
        return expandedCards;
    }

    private void distributeCardsToPlayer(Player player, List<Card> availableCards) {
        List<Card> hand = new ArrayList<>(availableCards.subList(0, 5));
        List<Card> drawPile = new ArrayList<>(availableCards.subList(5, 10));

        player.setHand(hand);
        player.setDrawPile(drawPile);
    }

}
