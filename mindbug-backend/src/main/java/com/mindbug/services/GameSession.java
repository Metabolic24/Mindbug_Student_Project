package com.mindbug.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindbug.models.Card;
import com.mindbug.models.Game;
import com.mindbug.models.Player;
import com.mindbug.services.wsmessages.WSMessageNewGame;
import com.mindbug.websocket.WSMessageManager;
import com.mindbug.websocket.WebsocketMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Component
public class GameSession {
    private Game game;

    private WSMessageManager gameWsMessageManager;

    private String wsChannel;

    private Long lastPlayerConfirmedJoin = null;

    private GameStatus status = GameStatus.NOT_STARTED;

    private List<Card> cards;

    private Map<String, Integer> cardCopiesMap = new HashMap<>();


    public GameSession(WSMessageManager gameWsMessageManager) {
        this.gameWsMessageManager = gameWsMessageManager;
    }

    public void initialize(Game game) {
        this.game = game;
        

    public GameSession(WSMessageManager gameWsMessageManager) {
        this.gameWsMessageManager = gameWsMessageManager;
    }

    public void initialize(Game game) {
        this.game = game;
        this.wsChannel = "/topic/game/" + game.getId();
        this.gameWsMessageManager.setChannel(wsChannel);
        distributionCard();
        initializeCardCopiesMap();
    }

    private void initializeCardCopiesMap() {
        cards = new ArrayList<>(Objects.requireNonNull(ReadCard()));
        for (Card card : cards) {
            cardCopiesMap.put(card.getName(), card.getCopies());
        }
        //System.out.println("Card copies map: " + cardCopiesMap);
    }

    private Set<String> selectedCards = new HashSet<>();

    private boolean checkCardCopies(Card card) {
        String cardName = card.getName();
        if (cardCopiesMap.containsKey(cardName)) {
            int copies = cardCopiesMap.get(cardName);
            return copies > 0;
        }
        return false;
    }

    private void updateCardCopies(Card card) {
        String cardName = card.getName();
        if (cardCopiesMap.containsKey(cardName)) {
            int copies = cardCopiesMap.get(cardName);
            if (copies == 2) {
                cardCopiesMap.put(cardName, 1);
                selectedCards.add(cardName);
            } else if (copies == 1) {
                cardCopiesMap.remove(cardName);
                selectedCards.add(cardName);
                cards.remove(card);
            }
        }
    }

    public void distributionCard() {
        if (game == null) {
            throw new IllegalStateException("Game is not initialized");
        }
        initializeCardCopiesMap();
        selectedCards.clear();

        if (cards.isEmpty()) {
            throw new IllegalStateException("No cards available for distribution");
        }
        System.out.println("Initial cards size: " + cards.size());

        Collections.shuffle(cards);

        game.getPlayer1().getHand().clear();
        game.getPlayer1().getDrawPile().clear();
        game.getPlayer2().getHand().clear();
        game.getPlayer2().getDrawPile().clear();

        distributeCardsToPlayer(game.getPlayer1());
        distributeCardsToPlayer(game.getPlayer2());
    }

    private void distributeCardsToPlayer(Player player) {
        Random random = new Random();
        int handSize = 5;
        int drawPileSize = 5;

        while (player.getHand().size() < handSize) {
            if (cards.isEmpty()) {
                throw new IllegalStateException("Not enough cards to distribute to player's hand");
            }
            Card card = cards.get(random.nextInt(cards.size()));
            if (checkCardCopies(card) && !selectedCards.contains(card.getName())) {
                player.getHand().add(card.getGameSessionCardId());
                updateCardCopies(card);
            }
        }

        while (player.getDrawPile().size() < drawPileSize) {
            if (cards.isEmpty()) {
                throw new IllegalStateException("Not enough cards to distribute to player's draw pile");
            }
            Card card = cards.get(random.nextInt(cards.size()));
            if (checkCardCopies(card) && !selectedCards.contains(card.getName())) {
                player.getDrawPile().add(card.getGameSessionCardId());
                updateCardCopies(card);
            }
        }

        System.out.println("Cards after distribution for player: " + player.getNickname() + " - Remaining: " + cards.size());
        System.out.println("Player's hand: " + player.getHand());
        System.out.println("Player's draw pile: " + player.getDrawPile());
        System.out.println("-----------------------------------------------");
    }

    private List<Card> ReadCard() {
        List<Card> originalCards = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/First_Contact.json");
        if (is == null) {
            System.out.println("First_Contact.json file not found!");
            return Collections.emptyList();
        } else {
            System.out.println("Successfully loaded First_Contact.json file.");
        }
        try {
            System.out.println("Reading First_Contact.json file...");
            originalCards = mapper.readValue(is, new TypeReference<List<Card>>() {});
            //System.out.println("Cards read: " + originalCards);
        } catch (IOException e) {
            System.err.println("Error reading First_Contact.json file: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>(originalCards);
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
    
}
