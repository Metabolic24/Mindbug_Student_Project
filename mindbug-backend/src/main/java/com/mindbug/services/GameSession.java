package com.mindbug.services;

import com.mindbug.models.Card;
import com.mindbug.models.Game;
import com.mindbug.models.Player;
import com.mindbug.services.wsmessages.WSMessageNewGame;
import com.mindbug.websocket.WSMessageManager;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Component
@Scope("prototype")
public class GameSession {
    private static final Logger logger = Logger.getLogger(GameSession.class.getName());
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

    public List<Player> getPlayers() {
        return Arrays.asList(this.game.getPlayer1(), this.game.getPlayer2());
    }

    public void initializeGame() {
        this.distributeCards();
    }

    private void distributeCards() {
        try {
            CardService cardService = new CardService();
            List<Card> cards = cardService.getCardsBySet("First_Contact");
            Collections.shuffle(cards);

            Map<Card, Integer> cardCopies = new HashMap<>();
            for (Card card : cards) {
                cardCopies.put(card, cardCopies.getOrDefault(card, 0) + 1);
            }

            distributeCardsToPlayer(this.game.getPlayer1(), cards, cardCopies);
            distributeCardsToPlayer(this.game.getPlayer2(), cards, cardCopies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCardCopies(Map<Card, Integer> cardCopies, Card card) {
        int copies = cardCopies.get(card);
        if (copies > 1) {
            cardCopies.put(card, copies - 1);
        } else {
            cardCopies.remove(card);
        }
    }

    private void distributeCardsToPlayer(Player player, List<Card> availableCards, Map<Card, Integer> cardCopies) throws IOException {
        List<Card> hand = new ArrayList<>();
        List<Card> drawPile = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Card card = availableCards.remove(0);
            updateCardCopies(cardCopies, card);
            hand.add(card);
        }

        for (int i = 0; i < 5; i++) {
            Card card = availableCards.remove(0);
            updateCardCopies(cardCopies, card);
            drawPile.add(card);
        }

        player.setHand(hand);
        player.setDrawPile(drawPile);
    }

    private void drawCard(Player player) {
        if (!player.getDrawPile().isEmpty()) {
            Card drawnCard = player.getDrawPile().remove(0);
            player.getHand().add(drawnCard);
        }
    }

    public Card getCardByName(Player player, String cardName) {
        return player.getHand().stream()
                .filter(card -> card.getName().equals(cardName))
                .findFirst()
                .orElse(null);
    }

    public void selectCard(Player player, String cardName) {
        logger.info("Player's hand: " + player.getHand().stream().map(Card::getName).collect(Collectors.joining(", ")));
        logger.info("Selecting card: " + cardName);

        Card selectedCard = getCardByName(player, cardName);
        if (selectedCard == null) {
            throw new IllegalArgumentException("Card not in hand: " + cardName);
        }

        player.setSelectedCard(selectedCard);
    }

    public void unselectCard(Player player) {
        player.setSelectedCard(null);
    }

    public void playCard(Player player) {
        Card selectedCard = player.getSelectedCard();
        if (selectedCard == null) {
            throw new IllegalStateException("No card selected.");
        }

        player.getHand().remove(selectedCard);
        player.setSelectedCard(null);

        if (player.getHand().size() < 5) {
            drawCard(player);
        }
    }
}
