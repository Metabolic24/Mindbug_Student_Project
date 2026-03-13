package org.metacorp.mindbug.utils;

import lombok.Setter;
import org.metacorp.mindbug.app.GameEngine;
import org.metacorp.mindbug.dto.player.PlayerLightDTO;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.AiPlayer;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.PlayCardService;
import org.metacorp.mindbug.service.game.StartService;

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

    public static Game createGame(String[] args) {
        PlayerService playerService = new PlayerService();
        boolean is2v2 = false;

        if (args != null && args.length > 0) {
            is2v2 = MODE_2V2.equalsIgnoreCase(args[0]);
        }

        return is2v2
                ? AppUtils.start2v2Game(playerService, false)
                : AppUtils.startGame(playerService, false);
    }


    /**
     * Start a new manual/auto game
     *
     * @return the created game
     */
    public static Game startGame(PlayerService playerService, boolean isAuto) {
        AppUtils.isAuto = isAuto;
        PlayerLightDTO player1 = playerService.createPlayer("Player1");
        PlayerLightDTO player2 = playerService.createPlayer("Player2");

        Game game = StartService.newGame(new AiPlayer(player1), new AiPlayer(player2));

        for (Player player : game.getPlayers()) {
            AppUtils.detailedSumUpPlayer(player);
        }

        System.out.println("\nDEBUT DU JEU !!!");
        nextTurn(game);

        return game;
    }

    public static Game start2v2Game(PlayerService playerService, boolean isAuto) {
        AppUtils.isAuto = isAuto;
        PlayerLightDTO player1 = playerService.createPlayer("Player1");
        PlayerLightDTO player2 = playerService.createPlayer("Player2");
        PlayerLightDTO player3 = playerService.createPlayer("Player3");
        PlayerLightDTO player4 = playerService.createPlayer("Player4");

        Game game = StartService.newGame(new Player(player1), new Player(player2), new Player(player3), new Player(player4));

        for (Player player : game.getPlayers()) {
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
        CardInstance card = (scanner == null) ? AiUtils.getRandomCard(hand) : getChosenCard(hand, scanner);
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
        CardInstance card = (scanner == null) ? AiUtils.getRandomCard(availableCards) : getChosenCard(availableCards, scanner);
        if (card != null) {
            System.out.printf("%s attaque avec la carte '%s'\n", currentPlayer.getName(), card.getCard().getName());
            AttackService.declareAttack(card, game);
        }
    }

    /**
     * Resolve an attack in an automatic game
     *
     * @param game the current game
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void resolveAttack(Game game) throws GameStateException, WebSocketException {
        resolveAttack(null, game);
    }

    /**
     * Resolve an attack in a manual/automatic game
     *
     * @param game    the current game
     * @param scanner the scanner to be used to read standard input (only for manual mode)
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void resolveAttack(Scanner scanner, Game game) throws GameStateException, WebSocketException {
        CardInstance blockingCard = null;

        List<Player> opponentPlayers = game.getAttackingCard().getOwner().getOpponents(game.getPlayers());
        for (Player opponentPlayer : opponentPlayers) {
            List<CardInstance> availableCards = AiUtils.getBlockersList(game);
            if (availableCards.isEmpty()) {
                System.out.printf("%s cannot block the attack\n", opponentPlayer.getName());
            } else {
                // Select a card and attack with it
                blockingCard = (scanner == null) ? AiUtils.getRandomCard(availableCards) : getChosenCard(availableCards, scanner, true);
                if (blockingCard != null) {
                    System.out.printf("%s blocks with card '%s'\n", opponentPlayer.getName(), blockingCard.getCard().getName());
                    break;
                } else {
                    System.out.printf("%s chose to not block \n", opponentPlayer.getName());
                }
            }
        }

        AttackService.resolveAttack(blockingCard, game);
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

    private static CardInstance getChosenCard(List<CardInstance> cards, Scanner scanner) {
        return getChosenCard(cards, scanner, false);
    }

    /**
     * Return the chosen card from the given list
     *
     * @param cards    the card list
     * @param scanner  the scanner to be used to read standard input (only for manual mode)
     * @param optional is the choice optional
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

    //TODO Cette méthode ne peut pas fonctionner comme ça...

    /**
     * Return the chosen card from the given list
     *
     * @param game the current game state
     * @param playerToChoose the player who needs to select an opponent
     * @return a random card from the list
     */
    public static Player selectOpponent(Game game, Player playerToChoose) {
        List<Player> listOpponents = playerToChoose.getOpponents(game.getPlayers());
        if (listOpponents.size() == 1) {
            return listOpponents.getFirst();
        }

        int index = 1;
        System.out.printf("\nPlease %s, choose an opponent to target : (only type the associated number)\n",
                playerToChoose.getName());
        for (Player opponent : listOpponents) {
            System.out.printf("\t(%d) - %s\n", index, opponent.getName());
            index++;
        }

        Player target;
        if (isAuto) {
            target = listOpponents.get(RND.nextInt(listOpponents.size()));
        } else {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    int choiceNumber = Integer.parseInt(scanner.nextLine());
                    if (1 <= choiceNumber && choiceNumber <= listOpponents.size()) {
                        target = listOpponents.get(choiceNumber - 1);
                        break;
                    } else {
                        System.err.println("Invalid number");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("You must type a valid number");
                }
            }
        }
        System.out.printf("\n%s chose to target %s\n", playerToChoose.getName(), target.getName());
        return target;
    }
}
