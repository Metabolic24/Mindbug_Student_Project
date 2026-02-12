package org.metacorp.mindbug.utils;

import lombok.Setter;
import org.metacorp.mindbug.app.GameEngine;
import org.metacorp.mindbug.dto.player.PlayerLightDTO;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.AttackService;
import org.metacorp.mindbug.service.game.PlayCardService;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.StartService;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

import javax.swing.text.html.StyleSheet.ListPainter;

/**
 * Utility class for ManualApp and AutoApp
 */
public final class AppUtils {

    private static final Random RND = new Random();

    @Setter
    private static boolean verbose = false;

    /**
     * Start a new manual/auto game
     *
     * @return the created game
     */
    public static Game startGame() {
        PlayerLightDTO player1 = PlayerService.createPlayer("Player1");
        PlayerLightDTO player2 = PlayerService.createPlayer("Player2");

        Game game = StartService.newGame(new Player(player1), new Player(player2));

        for (Player player : game.getPlayers()) {
            AppUtils.detailedSumUpPlayer(player);
        }

        System.out.println("\nDEBUT DU JEU !!!");
        nextTurn(game);

        return game;
    }

    public static Game start2v2Game(){
        PlayerLightDTO player1 = PlayerService.createPlayer("Player1");
        PlayerLightDTO player2 = PlayerService.createPlayer("Player2");
        PlayerLightDTO player3 = PlayerService.createPlayer("Player3");
        PlayerLightDTO player4 = PlayerService.createPlayer("Player4");

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
    public static void play(Game game) throws GameStateException {
        play(null, game);
    }

    /**
     * Play a card in a manual/automatic game
     *
     * @param game    the current game
     * @param scanner the scanner to be used to read standard input (only for manual mode)
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void play(Scanner scanner, Game game) throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        List<CardInstance> hand = currentPlayer.getHand();
        if (hand.isEmpty()) {
            System.out.println("Illegal move : cannot play cards if none in hand");
            return;
        }

        // Select a card and play it
        CardInstance card = (scanner == null) ? getRandomCard(hand) : getChosenCard(hand, scanner);
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
    public static void declareAttack(Game game) throws GameStateException {
        declareAttack(null, game);
    }

    /**
     * Declare an attack in a manual/automatic game
     *
     * @param game    the current game
     * @param scanner the scanner to be used to read standard input (only for manual mode)
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void declareAttack(Scanner scanner, Game game) throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();

        List<CardInstance> availableCards = currentPlayer.getBoard().stream().filter(CardInstance::isAbleToAttack).toList();
        if (availableCards.isEmpty()) {
            System.out.println("Illegal move : cannot attack while no card on the board");
            return;
        }

        // Select a card and attack with it
        CardInstance card = (scanner == null) ? getRandomCard(availableCards) : getChosenCard(availableCards, scanner);
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
    public static void resolveAttack(Game game) throws GameStateException {
        resolveAttack(null, game);
    }

    /**
     * Resolve an attack in a manual/automatic game
     *
     * @param game    the current game
     * @param scanner the scanner to be used to read standard input (only for manual mode)
     * @throws GameStateException if an error occurs during the game execution
     */
    public static void resolveAttack(Scanner scanner, Game game) throws GameStateException {
        boolean nobody_already_a_blocker = true;
        List<Player> ListopponentPlayer = game.getAttackingCard().getOwner().getOpponent(game.getPlayers());
        for (Player opponentPlayer : ListopponentPlayer) {
            if (nobody_already_a_blocker) {
                Stream<CardInstance> blockersStream = opponentPlayer.getBoard().stream().filter(CardInstance::isAbleToBlock);
                if (game.getAttackingCard().hasKeyword(CardKeyword.SNEAKY)) {
                    blockersStream = blockersStream.filter((card) -> card.hasKeyword(CardKeyword.SNEAKY));
                }

                List<CardInstance> availableCards = blockersStream.toList();
                if (availableCards.isEmpty()) {
                    System.out.printf("%s ne peut pas défendre\n", opponentPlayer.getName());
                    
                } else {
                    // Select a card and attack with it
                    CardInstance card = (scanner == null) ? getRandomCard(availableCards) : getChosenCard(availableCards, scanner,true);
                    if (card != null) {
                        System.out.printf("%s défend avec la carte '%s'\n", opponentPlayer.getName(), card.getCard().getName());
                        nobody_already_a_blocker=false;
                        AttackService.resolveAttack(card, game);
                    }
                    else {
                        System.out.printf("%s chosen not block \n", opponentPlayer.getName());
                    }
                    
                }
            }
        }
        if (nobody_already_a_blocker) AttackService.resolveAttack(null, game);//nobody have block this turn
        
    
    }

    /**
     * Print a detailed sum-up for the given player
     *
     * @param player the player to sum-up
     */
    public static void detailedSumUpPlayer(Player player) {
        System.out.printf("\n%s : %d PV, %d Mindbug(s), %d carte(s) restante(s)\n", player.getName(), player.getTeam().getLifePoints(), player.getMindBugs(), player.getDrawPile().size());
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
        } catch (GameStateException gst) {
            System.err.println(gst.getMessage());
            throw new RuntimeException(gst);
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
     * Return a random card from the given list
     *
     * @param cards the card list
     * @return a random card from the list
     */
    private static CardInstance getRandomCard(List<CardInstance> cards) {
        return cards.get(RND.nextInt(cards.size()));
    }

    /**
     * Return the chosen card from the given list
     *
     * @param cards   the card list
     * @param scanner the scanner to be used to read standard input (only for manual mode)
     * @param pass if  you want to pass the action
     * @return a random card from the list
     */
    private static CardInstance getChosenCard(List<CardInstance> cards, Scanner scanner, boolean pass) {
        System.out.println("Please choose a card : (only type the number)");
        if (pass) System.out.println("       (0) - Do nothing");// if you can pass the choice
        int index = 1;
        for (CardInstance card : cards) {
            System.out.printf("       (%d) - %s\n", index, card.getCard().getName());
            index++;
        }
      
        try {
            int choice_number = Integer.parseInt(scanner.nextLine());
            if (choice_number==0 && pass) {// if you want to pass the choice
                return null;
            }
            else {
                return cards.get(choice_number-1);
            }
            
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Choix de carte invalide");
            return null;
        }
    }
    private static CardInstance getChosenCard(List<CardInstance> cards, Scanner scanner) {
        return getChosenCard( cards, scanner, false);
    } 



    /**
     * Return the chosen card from the given list
     *
     * @param cards   the card list
     * @param scanner the scanner to be used to read standard input (only for manual mode)
     * @param pass if  you want to pass the action
     * @return a random card from the list
     */
    public static Player ChosenOpponent(Game game, Player player_who_targets ) {
      
        
        
        List<Player> listOpponents = player_who_targets.getOpponent(game.getPlayers());
        if (listOpponents.size()==1 ){
            return listOpponents.get(0);
        }


        Scanner scanner = new Scanner(System.in);
        Player target = null;
        while (true) {
            int index = 1;
            System.out.printf("\nPlease %s, choose an opponent to target : (only type the associated number)\n", player_who_targets.getName());
            for (Player opponent : listOpponents) {
                System.out.printf("       (%d) - %s\n", index, opponent.getName());
                index++;
            }
            if (game.isAuto()) {
                target = listOpponents.get(RND.nextInt(listOpponents.size()));
                break;
            }
            try {
                int choice_number = Integer.parseInt(scanner.nextLine());

                if  (1<=choice_number && choice_number<=listOpponents.size()) {
                    scanner =null;
                    target = listOpponents.get(choice_number-1);
                    break;
                }
                else {
                    System.err.println("Invalid number");
                }
            } catch (NumberFormatException e) {
                System.out.println("You must type a valid number");
                
            }
        }
        System.out.printf("\n%s chose to target %s\n", player_who_targets.getName(), target.getName());
        return target;
    }





    /**
     * @return a random boolean value
     */
    public static boolean nextBoolean() {
        return RND.nextBoolean();
    }



}
