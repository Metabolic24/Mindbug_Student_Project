package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.WebSocketService;
import org.metacorp.mindbug.utils.SetUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Utility service that starts a new game
 */
public class StartService {

    // Not to be used
    private StartService() {
        // Nothing to do
    }

    /**
     * Creates and start a new game for two players (using the default FIRST_CONTACT card set)
     *
     * @param player1 first player
     * @param player2 second player
     * @return the created game
     */
    public static Game startGame(Player player1, Player player2) {
        return startGame(new Game(player1, player2), CardSetName.FIRST_CONTACT);
    }

    /**
     * Creates and start a new game for two players (using the given card set)
     *
     * @param game the game to initialize
     * @param setName the card set name as CardSetName
     * @return the created game
     */
    public static Game startGame(Game game, CardSetName setName) {
        // Retrieve CardInstance from JSON configuration file and separate evolution cards from the other ones
        List<CardInstance> cards = game.getCards();
        SetUtils.getCardsFromConfig(setName.getKey()).forEach(cardInstance -> {
            if (cardInstance.getCard().isEvolution()) {
                game.getEvolutionCards().add(cardInstance);
            } else {
                cards.add(cardInstance);
            }
        });

        Collections.shuffle(cards);

        for (Player player : game.getPlayers()) {
            initDrawAndHand(player, cards);
            player.getTeam().setLifePoints(3);
        }

        game.setCurrentPlayer(getFirstPlayer(game));

        // Join the WebSocket channel of the game
        WebSocketService.initGameChannel(game);
        HistoryService.logStart(game);

        return game;
    }

    /**
     * Fill the hand and draw pile of the given player
     * @param player the player to be initialized
     * @param cards the remaining cards of the played set
     */
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

    /**
     * Get the first player of the game (should only be used once per game)
     * @param game the current game state
     * @return the first Player
     */
    private static Player getFirstPlayer(Game game) {
        Logger logger = game.getLogger();
        logger.info("Calculating first player...");

        List<Player> validPlayers = new ArrayList<>(game.getPlayers());
        while (validPlayers.size() != 1) {
            int higherPower = 0;
            List<Player> nextPlayers = new ArrayList<>();

            for (Player player : validPlayers) {
                // Get a random card from the remaining cards
                CardInstance bannedCard = banCard(game);
                logger.info("Banned card for {} : {} ({})", getLoggablePlayer(player), getLoggableCard(bannedCard), bannedCard.getPower());

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

        Player firstPlayer = validPlayers.getFirst();
        logger.info("First player will be {}", getLoggablePlayer(firstPlayer));

        return firstPlayer;
    }

    /**
     * Randomly choose a card and exclude it from the current game
     * @param game the current game state
     * @return the banned card
     */
    private static CardInstance banCard(Game game) {
        CardInstance chosenCard = game.getCards().removeFirst();
        game.getBannedCards().add(chosenCard);

        return chosenCard;
    }
}
