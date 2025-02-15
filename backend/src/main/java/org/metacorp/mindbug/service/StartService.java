package org.metacorp.mindbug.service;

import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.CardUtils;

import java.util.*;

/**
 * Utility service that starts a new game
 */
public class StartService {

    private static final Map<String, String> cardSetsMap = Map.of(
        CardUtils.FIRST_CONTACT, "first_contact.json"
    );

    // Not to be used
    private StartService() {
        // Nothing to do
    }

    /**
     * Creates and start a new game for two players (using the default FIRST_CONTACT card set)
     * @param player1 first player name
     * @param player2 second player name
     * @return the created game
     */
    public static Game newGame(String player1, String player2) {
        return newGame(player1, player2, CardUtils.FIRST_CONTACT);
    }

    /**
     * Creates and start a new game for two players (using the given card set)
     * @param player1 first player name
     * @param player2 second player name
     * @param cardSetKey the card set key
     * @return the created game
     */
    public static Game newGame(String player1, String player2, String cardSetKey) {
        Game game = new Game(player1, player2);

        String cardSetFile = cardSetsMap.get(cardSetKey);
        if (cardSetFile == null) {
            throw new IllegalArgumentException("Invalid card set key: " + cardSetKey);
        }

        List<CardInstance> cards = CardUtils.getCardsFromConfig("first_contact.json");
        Collections.shuffle(cards);
        game.setCards(cards);

        for (Player player : game.getPlayers()) {
            initDrawAndHand(player, cards);
            player.getTeam().setLifePoints(3);
        }

        game.setCurrentPlayer(getFirstPlayer(game));

        return game;
    }

    // Fill the hand and draw pile of the given player
    private static void initDrawAndHand(Player player, List<CardInstance> cards) {
        List<CardInstance> hand = player.getHand();
        List<CardInstance> drawPile = player.getDrawPile();

        while (hand.size() != 5 && drawPile.size() != 5) {
            CardInstance currentCard = cards.removeFirst();
            currentCard.setOwner(player);
            hand.add(currentCard);

            currentCard = cards.removeFirst();
            currentCard.setOwner(player);
            drawPile.add(currentCard);
        }
    }

    // Return the first player of the game (should only be used once per game)
    private static Player getFirstPlayer(Game game) {
        System.out.println("Calcul du premier joueur :");

        List<Player> validPlayers = new ArrayList<>(game.getPlayers());
        while (validPlayers.size() != 1) {
            int higherPower = 0;
            List<Player> nextPlayers = new ArrayList<>();

            for (Player player : validPlayers) {
                // Get a random card from the remaining cards
                CardInstance bannedCard = banCard(game);
                System.out.printf("\t%s %s %d\n", player.getName(), bannedCard.getCard().getName(), bannedCard.getPower());

                if (bannedCard.getPower() < higherPower) {
                    // Current player will not be the first one
                    continue;
                } else if (bannedCard.getPower() > higherPower) {
                    // Update higherPower value and clean the next players set
                    higherPower = bannedCard.getPower();
                    nextPlayers.clear();
                }

                nextPlayers.add(player);
            }

            validPlayers = nextPlayers;
        }

        System.out.printf(" -> %s sera le premier joueur\n", validPlayers.getFirst().getName());

        return validPlayers.getFirst();
    }

    // Randomly choose a card and exclude it from the current game
    private static CardInstance banCard(Game game) {
        CardInstance chosenCard = game.getCards().removeFirst();
        game.getBannedCards().add(chosenCard);

        return chosenCard;
    }
}
