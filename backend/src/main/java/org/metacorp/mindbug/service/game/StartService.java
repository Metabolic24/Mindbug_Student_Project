package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.model.CardSetName;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.model.player.Team;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.WebSocketService;
import org.metacorp.mindbug.utils.CardUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public static Game newGame(Player player1, Player player2) {
        return newGame(player1, player2, CardSetName.FIRST_CONTACT);
    }

    /**
     * Creates and start a new game for four players (using the default FIRST_CONTACT card set)
     *
     * @param player1 first player
     * @param player2 second player
     * @param player3 third player
     * @param player4 fourth player
     * @return the created game
     */
    public static Game newGame(Player player1, Player player2, Player player3, Player player4) {
        return newGame(player1, player2, player3, player4, CardSetName.FIRST_CONTACT);
    }

    /**
     * Creates and start a new game for two players (using the given card set)
     *
     * @param player1 first player name
     * @param player2 second player name
     * @param setName the card set name as CardSetName
     * @return the created game
     */
    public static Game newGame(Player player1, Player player2, CardSetName setName) {
        Game game = new Game(player1, player2);

        return createAndInitGame(game, setName);
    }

    /**
     * Create and start a new game for four players (2v2) (using the given card set)
     * The players 1 & 3 are together against the players 2 & 4
     */
    public static Game newGame(Player p1, Player p2, Player p3, Player p4, CardSetName setName) {
        Game game = new Game(p1, p2, p3, p4);

        return createAndInitGame(game, setName);
    }

    /**
     * Internal method for factoring the common initialization
     */
    private static Game createAndInitGame(Game game, CardSetName setName) {
        CardUtils.getCardsFromConfig(setName.getKey()).forEach(cardInstance -> {
            if (cardInstance.getCard().isEvolution()) {
                game.getEvolutionCards().add(cardInstance);
            } else {
                game.getCards().add(cardInstance);
            }
        });

        Collections.shuffle(game.getCards());

        // 2. Initialization of players and PVs
        // We use a Set to avoid resetting the HP twice for the same team
        Set<Team> initializedTeams = new HashSet<>();
        
        for (Player player : game.getPlayers()) {
            initDrawAndHand(player, game.getCards());
            
            // If the team has not yet been initialized, we set the HP to 3 (or more for 2v2)
            if (!initializedTeams.contains(player.getTeam())) {
                player.getTeam().setLifePoints(3); 
                initializedTeams.add(player.getTeam());
            }
        }

        game.setCurrentPlayer(getFirstPlayer(game));
        WebSocketService.initGameChannel(game);
        HistoryService.logStart(game);

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
