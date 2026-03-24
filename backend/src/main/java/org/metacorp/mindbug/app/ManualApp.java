package org.metacorp.mindbug.app;

import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.AbstractChoice;
import org.metacorp.mindbug.model.choice.BlockChoice;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.choice.HunterChoice;
import org.metacorp.mindbug.model.choice.MindbugChoice;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.game.ChoiceService;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Mindbug application that expects player(s) to type command in the console to make the game progress
 */
public class ManualApp {

    private static final String AVAILABLE_ACTIONS = "Actions possibles : play, p, attack, a, sumup, s, details, d, stop, exit\n";

    static void main(String[] args) throws CardSetException {
        Game game = AppUtils.createGame(args, false);

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
                    case FRENZY, BOOLEAN, MINDBUG -> {
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
                    case BLOCK -> {
                        System.out.print("Resolving block choice :");
                        try {
                            int choiceNumber = Integer.parseInt(scanner.nextLine());
                            if (choiceNumber == 0) {
                                System.out.print("no block\n");
                                ChoiceService.resolveChoice(null, game);
                            } else {
                                CardInstance chosenCard = ((BlockChoice) choice).getBlockersMap().get(choice.getPlayerToChoose()).get(choiceNumber - 1);
                                System.out.printf("blocking with %s\n", chosenCard.getCard().getName());
                                ChoiceService.resolveChoice(chosenCard.getUuid(), game);
                            }
                        } catch (NumberFormatException | IndexOutOfBoundsException e) {
                            throw new GameStateException("Choix invalide");
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
            case SIMULTANEOUS -> System.out.println("Choose the effect to resolve first (only type the card ID) :");
            case TARGET -> {
                System.out.println("Choose the target(s) (type the card(s) ID separated by 'space' character) :");
                ((TargetChoice) choice).getAvailableTargets().forEach(target ->
                        System.out.println(
                                "- " + target.getCard().getName()
                                        + " (id: " + target.getUuid() + ")"
                        )
                );
            }
            case HUNTER -> {
                System.out.println("Choose the target to hunt (OPTIONAL ; only type the card ID) : ");
                ((HunterChoice) choice).getAvailableTargets().forEach(target ->
                        System.out.println(
                                "- " + target.getCard().getName()
                                        + " (id: " + target.getUuid() + ")"
                        )
                );
            }
            case FRENZY -> System.out.println("Do you want to attack again? (Y/N)");
            case BOOLEAN -> {
                BooleanChoice booleanChoice = (BooleanChoice) choice;
                String message = switch (booleanChoice.getSourceCard().getCard().getId()) {
                    case 40 -> "Do you want to play the stolen card " + booleanChoice.getCard().getCard().getName();
                    case 41 -> "Do you want to revive Hyenix";
                    // Should not happen
                    default -> "";
                };
                System.out.println(message + "? (Y/N)");
            }
            case MINDBUG ->
                    System.out.printf("Do you want to mindbug card %s? (Y/N)\n", ((MindbugChoice) choice).getPlayedCard().getCard().getName());
            case BLOCK -> {
                BlockChoice blockChoice = (BlockChoice) choice;
                System.out.printf("Do you want to block the attack of %s(%d) ? (OPTIONAL ; only type the card ID)\n", blockChoice.getAttackingCard().getCard().getName(), blockChoice.getAttackingCard().getPower());
                System.out.println("\t(0) - Do nothing"); // if you can pass the choice

                List<CardInstance> availableBlockers = blockChoice.getBlockersMap().get(choice.getPlayerToChoose());
                for (int i = 1; i <= availableBlockers.size(); i++) {
                    System.out.printf("\t(%d) - %s\n", i, availableBlockers.get(i - 1).getCard().getName());
                }
            }
            default -> {
                // Should not happen
            }
        }
    }
}
