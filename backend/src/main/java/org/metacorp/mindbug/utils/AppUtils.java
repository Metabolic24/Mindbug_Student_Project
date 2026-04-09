package org.metacorp.mindbug.utils;

import lombok.Setter;
import org.metacorp.mindbug.app.GameEngine;
import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardSetName;
import org.metacorp.mindbug.model.player.AiPlayer;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.CardSetService;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.CardService;
import org.metacorp.mindbug.service.game.PlayCardService;
import org.metacorp.mindbug.service.game.StartService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Utility class for ManualApp and AutoApp
 */
public final class AppUtils {

    private static final Random RND = new Random();

    private static final String MODE_2V2 = "2v2";

    @Setter
    private static boolean verbose = false;

    private static boolean isAuto = false;

    /**
     * Create a new game to be used by ManualApp or AutoApp ONLY
     *
     * @param args   the main class arguments
     * @param isAuto is it an automatic game or not
     * @return the created game
     * @throws CardSetException if an error occurs ....TODO
     */
    public static Game createGame(String[] args, boolean isAuto) throws CardSetException {
        PlayerService playerService = new PlayerService();
        StartService startService = new StartService();
        startService.cardSetService = new CardSetService();
        boolean is2v2 = false;

        if (args != null && args.length > 0) {
            is2v2 = MODE_2V2.equalsIgnoreCase(args[0]);
        }

        return AppUtils.startGame(playerService, startService, isAuto, is2v2);
    }

    /**
     * Start a new manual/auto game
     *
     * @return the created game
     */
    public static Game startGame(PlayerService playerService, StartService startService, boolean isAuto, boolean is2v2) throws CardSetException {
        AppUtils.isAuto = isAuto;

        List<Player> players = new ArrayList<>(List.of(
                new AiPlayer(playerService.createPlayer("Player1")),
                new AiPlayer(playerService.createPlayer("Player2"))
        ));

        if (is2v2) {
            players.add(new AiPlayer(playerService.createPlayer("Player3")));
            players.add(new AiPlayer(playerService.createPlayer("Player4")));
        }

        Game game = startService.startGame(new Game(players), CardSetName.FIRST_CONTACT);

        for (Player player : players) {
            AppUtils.detailedSumUpPlayer(player);
        }

        System.out.println("\nDEBUT DU JEU !!!");
        nextTurn(game);

        return game;
    }

    /**
     * Play a card in an automatic game
     *
     * @param game the current game
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void play(Game game) throws GameStateException, WebSocketException {
        play(null, game);
    }

    /**
     * Play a card in a manual/automatic game
     *
     * @param game    the current game
     * @param scanner the scanner to be used to read standard input (only for manual mode)
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void play(Scanner scanner, Game game) throws GameStateException, WebSocketException {
        Player currentPlayer = game.getCurrentPlayer();
        List<CardInstance> hand = currentPlayer.getHand();
        if (hand.isEmpty()) {
            System.out.println("Illegal move : cannot play cards if none in hand");
            return;
        }

        // Select a card and play it
        CardInstance card = (scanner == null) ? CardService.getRandomCard(hand) : getChosenCard(hand, scanner);
        if (card != null) {
            System.out.printf("%s joue la carte '%s'\n", currentPlayer.getName(), card.getCard().getName());
            PlayCardService.pickCard(card, game);
        }
    }

    /**
     * Declare an attack in an automatic game
     *
     * @param game the current game
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void declareAttack(Game game) throws GameStateException, WebSocketException {
        declareAttack(null, game);
    }

    /**
     * Declare an attack in a manual/automatic game
     *
     * @param game    the current game
     * @param scanner the scanner to be used to read standard input (only for manual mode)
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void declareAttack(Scanner scanner, Game game) throws GameStateException, WebSocketException {
        Player currentPlayer = game.getCurrentPlayer();

        List<CardInstance> availableCards = currentPlayer.getBoard().stream().filter(CardInstance::isAbleToAttack).toList();
        if (availableCards.isEmpty()) {
            System.out.println("Illegal move : cannot attack while no card on the board");
            return;
        }

        // Select a card and attack with it
        CardInstance card = (scanner == null) ? CardService.getRandomCard(availableCards) : getChosenCard(availableCards, scanner);
        if (card != null) {
            System.out.printf("%s attaque avec la carte '%s'\n", currentPlayer.getName(), card.getCard().getName());
            AttackService.declareAttack(card, game);
        }
    }

    /**
     * Print a detailed sum-up for the given player
     *
     * @param player the player to sum-up
     */
    public static void detailedSumUpPlayer(Player player) {
        System.out.printf("\n%s (%s) : %d PV, %d Mindbug(s), %d carte(s) restante(s)\n", player.getName(), player.getUuid(),
                player.getTeam().getLifePoints(), player.getMindBugs(), player.getDrawPile().size());
        displayCards(player.getHand(), "Main");
        displayCards(player.getBoard(), "Terrain");
        displayCards(player.getDiscardPile(), "Défausse");
    }

    /**
     * Print the details for the given list of cards
     *
     * @param cards    the card list
     * @param location the location of the cards
     */
    private static void displayCards(List<CardInstance> cards, String location) {
        if (!cards.isEmpty()) {
            System.out.printf("\t%s : %d cartes\n", location, cards.size());
            for (CardInstance card : cards) {
                System.out.printf("\t- %s (%d) %s \n", card.getCard().getName(), card.getPower(),
                        card.getKeywords().isEmpty() ? "" : card.getKeywords().toString());
            }
        }
    }

    /**
     * Print specific data when a turn ends
     *
     * @param game the current game
     */
    public static void nextTurn(Game game) {
        for (Player player : game.getPlayers()) {
            if (verbose) {
                detailedSumUpPlayer(player);
                System.out.println();
            }
        }

        System.out.printf("\n<<<<< Au tour de %s >>>>>\n", game.getCurrentPlayer().getName());
    }

    /**
     * Run a manual/automatic game in a Mindbug application
     *
     * @param game   the game to run
     * @param engine the game engine to use (manual/automatic)
     */
    public static void runAndCheckErrors(Game game, GameEngine engine) {
        try {
            engine.run();
        } catch (GameStateException | WebSocketException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (Throwable t) {
            System.out.println("=================================");
            System.out.println("Une erreur s'est produite (cf contexte ci-dessous)");

            for (Player player : game.getPlayers()) {
                AppUtils.detailedSumUpPlayer(player);
                System.out.println("=================================");
            }

            System.out.println(game);

            throw t;
        }
    }

    /**
     * Return the chosen card from the given list
     *
     * @param cards   the card list
     * @param scanner the scanner to be used to read standard input (only for manual mode)
     * @return a random card from the list
     */
    private static CardInstance getChosenCard(List<CardInstance> cards, Scanner scanner) {
        return getChosenCard(cards, scanner, false);
    }

    /**
     * Return the chosen card from the given list
     *
     * @param cards    the card list
     * @param scanner  the scanner to be used to read standard input (only for manual mode)
     * @param optional is it an optional choice
     * @return a random card from the list
     */
    private static CardInstance getChosenCard(List<CardInstance> cards, Scanner scanner, boolean optional) {
        System.out.println("Please choose a card : (only type the number)");
        if (optional) {
            System.out.println("\t(0) - Do nothing"); // if you can pass the choice
        }

        int index = 1;
        for (CardInstance card : cards) {
            System.out.printf("\t(%d) - %s\n", index, card.getCard().getName());
            index++;
        }

        try {
            int choiceNumber = Integer.parseInt(scanner.nextLine());
            if (choiceNumber == 0 && optional) {
                // Skip the choice only if optional
                return null;
            } else {
                return cards.get(choiceNumber - 1);
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Choix de carte invalide");
            return null;
        }
    }

    
}
