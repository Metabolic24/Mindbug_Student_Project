package org.metacorp.mindbug;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/** Main class that manages game workflow */
@Getter
@Setter
public class Game {

    private List<Player> players;
    private Player currentPlayer;

    private List<CardInstance> cards;
    private List<CardInstance> bannedCards;

    /** Empty constructor (WARNING : a game is not meant to be reused) */
    public Game(String player1, String player2) {
        bannedCards = new ArrayList<>();
        players = new ArrayList<>(2);

        players.add(new Player(player1));
        players.add(new Player(player2));

        start();
    }

    // Start a new game
    private void start() {
        cards = Utils.getCardsFromConfig("default.json");

        Collections.shuffle(cards);

        for (Player player : players) {
            initDrawAndHand(player);
            player.getTeam().setLifePoints(3);
        }

        this.currentPlayer = getFirstPlayer();
    }

    // Return the first player of the game (should only be used once per game)
    private Player getFirstPlayer() {
        List<Player> validPlayers = new ArrayList<>(players);
        while (validPlayers.size() != 1) {
            int power = 0;
            List<Player> nextPlayers = new ArrayList<>();

            for (Player player : validPlayers) {
                CardInstance firstPlayerCard = banCard();

                if (firstPlayerCard.getPower() < power) {
                    continue;
                } else if (firstPlayerCard.getPower() > power) {
                    power = firstPlayerCard.getPower();
                    nextPlayers.clear();
                }

                nextPlayers.add(player);
            }

            validPlayers = nextPlayers;
        }

        return validPlayers.getFirst();
    }

    // Randomly choose a card and ban it
    private CardInstance banCard() {
        int cardIndex = new Random().nextInt(cards.size() - 1);
        CardInstance chosenCard = cards.remove(cardIndex);
        bannedCards.add(chosenCard);

        return chosenCard;
    }

    // Fill the hand and draw pile of the given player
    private void initDrawAndHand(Player player) {
        List<CardInstance> hand = player.getHand();
        while (hand.size() != 5) {
            int cardIndex = new Random().nextInt(cards.size() - 1);
            hand.add(cards.remove(cardIndex));
        }

        List<CardInstance> drawPile = player.getDrawPile();
        while (drawPile.size() != 5) {
            int cardIndex = new Random().nextInt(cards.size() - 1);
            drawPile.add(cards.remove(cardIndex));
        }
    }
}
