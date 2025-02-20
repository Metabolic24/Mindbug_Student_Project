package com.mindbug.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindbug.models.Card;
import com.mindbug.models.Game;
import com.mindbug.models.GameSessionCard;
import com.mindbug.models.Player;
import com.mindbug.services.wsmessages.WSMessageNewGame;
import com.mindbug.websocket.WSMessageManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
        //initializeCardCopiesMap();
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
                GameSessionCard gameSessionCard = new GameSessionCard(card);
                gameSessionCard.setNumber(card.getGameSessionCardId().getNumber()); // Set the number field
                player.getHand().add(gameSessionCard);
                updateCardCopies(card);
            }
        }

        while (player.getDrawPile().size() < drawPileSize) {
            if (cards.isEmpty()) {
                throw new IllegalStateException("Not enough cards to distribute to player's draw pile");
            }
            Card card = cards.get(random.nextInt(cards.size()));
            if (checkCardCopies(card) && !selectedCards.contains(card.getName())) {
                GameSessionCard gameSessionCard = new GameSessionCard(card);
                gameSessionCard.setNumber(card.getGameSessionCardId().getNumber());// Set the number field
                player.getDrawPile().add(gameSessionCard);
                updateCardCopies(card);
            }
        }
        System.out.println("Player " + player.getNickname() + " hand: " + player.getHand());
        System.out.println("Player " + player.getNickname() + " draw pile: " + player.getDrawPile());
        System.out.println("Card con lai " + cards.size());
    }

    private List<Card> ReadCard() {
        List<Card> originalCards = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/sets/First_Contact.json");
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

    public void selectCard(String playerNickname, int cardNumber) {
        Player player = getPlayerByNickname(playerNickname);
        if (player == null) {
            throw new IllegalArgumentException("Invalid player nickname");
        }
        GameSessionCard card = getCardByNumber(cardNumber, player.getHand());
        if (card == null) {
            throw new IllegalArgumentException("Card not in player's hand");
        }
        player.setSelectedCard(cardNumber);
    }

    private GameSessionCard getCardByNumber(int number, List<GameSessionCard> hand) {
        for (GameSessionCard card : hand) {
            if (card.getNumber() == number) {
                return card;
            }
        }
        return null;
    }

    public void unselectCard(String playerNickname) {
        Player player = getPlayerByNickname(playerNickname);
        if (player == null) {
            throw new IllegalArgumentException("Invalid player nickname");
        }
        player.setSelectedCard(null);
    }

    public Player getPlayerByNickname(String playerNickname) {

        if (playerNickname.equals(game.getPlayer1().getNickname())) {
            return game.getPlayer1();

        } else if (playerNickname.equals(game.getPlayer2().getNickname())) {
            return game.getPlayer2();
        }
        return null;
    }

    public void playCard(String playerNickname) {
        Player player = getPlayerByNickname(playerNickname);
        if (player == null) {
            throw new IllegalArgumentException("Invalid player nickname");
        }
        Integer selectedCardNumber = player.getSelectedCard();
        if (selectedCardNumber == null) {
            throw new IllegalStateException("No card selected");
        }
        GameSessionCard selectedCard = getCardByNumber(selectedCardNumber, player.getHand());
        if (selectedCard == null) {
            throw new IllegalStateException("Selected card not found in hand");
        }
        player.getHand().remove(selectedCard);
        player.getBoard().add(selectedCard);
        player.setSelectedCard(null);

        if (player.getHand().size() < 5 && !player.getDrawPile().isEmpty()) {
            GameSessionCard newCard = player.getDrawPile().remove(0);
            player.getHand().add(newCard);
        }
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


    public String getPlayerHandCard(String playerNickname) {
        Player player = getPlayerByNickname(playerNickname);
        if (player == null) {
            throw new IllegalArgumentException("Invalid player nickname");
        }
        List<GameSessionCard> hand = player.getHand();
        StringBuilder handWithNames = new StringBuilder();
        for (GameSessionCard gameSessionCard : hand) {
            Card card = gameSessionCard.getCard();
            if (card != null) {
                handWithNames.append("Card ID: ").append(gameSessionCard.getNumber())
                             .append(", Card Name: ").append(card.getName())
                             .append("\n");
            }
        }
        return handWithNames.toString();
    }
}
