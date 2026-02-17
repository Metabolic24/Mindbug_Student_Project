package org.metacorp.mindbug.app;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.choice.AbstractChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.service.game.ChoiceService;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Mindbug application that expects player(s) to type command in the console to make the game progress
 */
public class ManualApp {

    private static final String AVAILABLE_ACTIONS = "Actions possibles : play, p, attack, a, sumup, s, details, d, stop, exit\n";

    static void main() {
        PlayerService playerService = new PlayerService();
        Game game = AppUtils.startGame(playerService);

        System.out.println(AVAILABLE_ACTIONS);

        AppUtils.runAndCheckErrors(game, () -> {
            Scanner scanner = new Scanner(System.in);
            boolean ok;

            do {
                ok = resolveTurn(scanner, game);
            } while (ok);
        });
    }

    /**
     * Processes the command written by the user
     *
     * @param scanner the input scanner
     * @param game    the current game instance
     * @return true if command has been successfully processed, false otherwise
     * @throws GameStateException if the game reaches an inconsistant state
     */
    private static boolean resolveTurn(Scanner scanner, Game game) throws GameStateException, WebSocketException {
        boolean turnResolved = false;

        String input = scanner.nextLine();
        switch (input.toLowerCase()) {
            case "play", "p":
                // Play a card
                AppUtils.play(scanner, game);
                resolveChoices(scanner, game);

                turnResolved = true;
                break;
            case "attack", "a":
                // Declare attack
                AppUtils.declareAttack(scanner, game);
                resolveChoices(scanner, game);

                // Resolve attack or Frenzy case
                while (game.getAttackingCard() != null && !game.isFinished()) {
                    AppUtils.resolveAttack(scanner, game);
                    resolveChoices(scanner, game);
                }

                turnResolved = true;
                break;
            case "sumup", "s":
                System.out.println("=========================================");
                for (Player player : game.getPlayers()) {
                    sumUpPlayer(player);
                }
                System.out.println("=========================================\n");
                System.out.printf("Au tour de %s\n", game.getCurrentPlayer().getName());
                break;
            case "details", "d":
                System.out.println("=========================================");
                for (Player player : game.getPlayers()) {
                    AppUtils.detailedSumUpPlayer(player);
                    System.out.println("=========================================");
                }

                System.out.printf("Au tour de %s\n", game.getCurrentPlayer().getName());
                if (game.getChoice() != null) {
                    System.out.printf("Choix à résoudre : %s\n", game.getChoice().getType());
                }
                break;
            case "stop", "exit":
                return false;
            default:
                System.err.println("Action invalide");
        }

        boolean finished = game.isFinished();
        if (!finished) {
            if (turnResolved) {
                AppUtils.nextTurn(game);
            }

            System.out.println(AVAILABLE_ACTIONS);
        }

        return !finished;
    }

    /**
     * Resolve zero, one or multiple choices
     *
     * @param scanner the input scanner
     * @param game    the current game
     */
    private static void resolveChoices(Scanner scanner, Game game) {
        while (game.getChoice() != null && !game.isFinished()) {
            printChoice(game.getChoice());
            resolveChoice(scanner, game);
        }
    }

    /**
     * Resolve a choice
     *
     * @param scanner the input scanner
     * @param game    the current game
     */
    private static void resolveChoice(Scanner scanner, Game game) {
        AbstractChoice<?> choice = game.getChoice();
        if (choice == null) {
            System.err.println("Action invalide");
        } else {
            String input = scanner.nextLine();

            try {
                switch (choice.getType()) {
                    case SIMULTANEOUS -> {
                        System.out.println("Résolution d'un choix d'ordonnancement d'effets simultanés");

                        SimultaneousEffectsChoice simultaneousEffectsChoice = (SimultaneousEffectsChoice) choice;
                        if (simultaneousEffectsChoice.getEffectsToSort().stream()
                                .noneMatch(effectsToApply -> effectsToApply.getCard().getUuid().toString().equals(input))) {
                            throw new GameStateException("Choix invalide");
                        }

                        System.out.printf("1er effet à résoudre : %s\n", input);

                        ChoiceService.resolveChoice(UUID.fromString(input), game);
                    }
                    case TARGET -> {
                        System.out.println("Résolution d'un choix de cible(s)");

                        TargetChoice targetChoice = (TargetChoice) choice;
                        String[] tokens = input.split(" ");

                        for (String token : tokens) {
                            if (targetChoice.getAvailableTargets().stream()
                                    .noneMatch(target -> target.getUuid().toString().equals(token))) {
                                throw new GameStateException("Choix invalide");
                            }
                        }

                        System.out.printf("Cible(s) choisie(s) : %s\n", Arrays.toString(tokens));

                        ChoiceService.resolveChoice(Stream.of(tokens).map(UUID::fromString).toList(), game);
                    }
                    case HUNTER -> {
                        System.out.println("Résolution d'un choix de cible d'attaque");
                        HunterChoice hunterChoice = (HunterChoice) choice;

                        if (input != null && !input.isBlank()) {
                            if (hunterChoice.getAvailableTargets().stream()
                                    .noneMatch(target -> target.getUuid().toString().equals(input))) {
                                throw new GameStateException("Choix invalide");
                            }

                            System.out.printf("Cible sélectionnée : %s\n", input);
                            ChoiceService.resolveChoice(UUID.fromString(input), game);
                        } else {
                            ChoiceService.resolveChoice(null, game);
                        }
                    }
                    case FRENZY, BOOLEAN -> {
                        System.out.printf("Résolution d'un choix booléen de type %s\n", choice.getType());

                        switch ((input.toLowerCase())) {
                            case "y", "o", "yes", "oui" -> {
                                System.out.println("Valeur choisie : OUI");
                                ChoiceService.resolveChoice(true, game);
                            }
                            case "n", "no", "non" -> {
                                System.out.println("Valeur choisie : NON");
                                ChoiceService.resolveChoice(false, game);
                            }
                            default -> throw new GameStateException("Choix invalide");
                        }
                    }
                    default -> {
                        // Should not happen
                    }
                }
            } catch (GameStateException | WebSocketException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Print a sum-up of the player status
     *
     * @param player the player related to the sum-up to produce
     */
    private static void sumUpPlayer(Player player) {
        System.out.printf("%s (%d PV, %d Mindbug(s), Main : %d, Terrain : %d, Défausse : %d, Pioche : %d\n",
                player.getName(), player.getTeam().getLifePoints(), player.getMindBugs(), player.getHand().size(), player.getBoard().size(),
                player.getDiscardPile().size(), player.getDrawPile().size());
    }

    /**
     * Print the choice sum-up
     *
     * @param choice the choice to print
     */
    private static void printChoice(AbstractChoice<?> choice) {
        switch (choice.getType()) {
            case SIMULTANEOUS ->
                    System.out.println("Veuillez choisir l'effet à résoudre en premier : (only type the ID)");
            case TARGET ->
                    System.out.println("Veuillez choisir la/les cibles : (type the card(s) ID separated by 'space' character)");
            case HUNTER -> System.out.println("Veuillez choisir la cible à chasser (si souhaité) : (only type the ID)");
            case FRENZY -> System.out.println("Voulez-vous attaquer à nouveau? (O/N)");
            case BOOLEAN -> System.out.println("Voulez-vous faire revenir Hyénix? (O/N)");
            default -> {
                // Should not happen
            }
        }
    }
}
