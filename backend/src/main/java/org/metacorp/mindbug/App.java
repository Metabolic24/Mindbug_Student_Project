package org.metacorp.mindbug;

import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.EffectToApply;
import org.metacorp.mindbug.choice.IChoice;
import org.metacorp.mindbug.choice.bool.BooleanChoice;
import org.metacorp.mindbug.choice.frenzy.FrenzyAttackChoice;
import org.metacorp.mindbug.choice.simultaneous.SimultaneousEffectsChoice;
import org.metacorp.mindbug.choice.target.TargetChoice;
import org.metacorp.mindbug.player.Player;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {
        Game game = new Game("Player1", "Player2");

        System.out.println("Début du jeu !!!");

        for (Player player : game.getPlayers()) {
            System.out.printf("\n%s : Main\n", player.getName());
            for (CardInstance card : player.getHand()) {
                System.out.printf("   - %s (%d) : %s \n", card.getCard().getName(), card.getPower(), card.getKeywords().toString());
            }
        }

        Scanner scanner = new Scanner(System.in);
        boolean ok = true;

        while (ok) {
            Player currentPlayer = game.getCurrentPlayer();
            System.out.printf("Au tour de %s\n", currentPlayer.getName());

            ok = processInput(scanner.nextLine(), game);
        }
    }

    private static boolean processInput(String input, Game game) {
        Player currentPlayer = game.getCurrentPlayer();

        String[] tokens = input.split(" ");
        switch (tokens[0]) {
            case "j":
                System.out.printf("%s joue la carte '%s'\n", currentPlayer.getName(), currentPlayer.getHand().getFirst().getCard().getName());
                game.pickCard(currentPlayer.getHand().getFirst());
                game.playCard(currentPlayer.getHand().getFirst(), false);
                break;
            case "a":
                System.out.printf("%s attaque avec la carte '%s'\n", currentPlayer.getName(), currentPlayer.getBoard().getFirst().getCard().getName());

                game.declareAttack(currentPlayer.getBoard().getFirst());
                break;
            case "ra":
                Player opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
                if (opponentPlayer.getBoard().isEmpty()) {
                    System.out.printf("%s ne peut pas défendre\n", opponentPlayer.getName());
                    game.resolveAttack(null);
                } else {
                    System.out.printf("%s défend avec la carte '%s'\n", opponentPlayer.getName(), opponentPlayer.getBoard().getFirst().getCard().getName());
                    game.resolveAttack(opponentPlayer.getBoard().getFirst());
                }

                break;
            case "rc":
                IChoice<?> choice = game.getCurrentChoice();

                if (choice == null) {
                    System.err.println("Action invalide");
                } else {
                    switch (choice.getType()) {
                        case SIMULTANEOUS -> {
                            System.out.println("Résolution d'un choix d'ordonnancement d'effets simultanés");

                            SimultaneousEffectsChoice simultaneousEffectsChoice = (SimultaneousEffectsChoice) choice;
                            System.out.printf("Ordre choisi : %s\n", simultaneousEffectsChoice.getEffectsToSort());

                            game.applyChoice(simultaneousEffectsChoice.getEffectsToSort().stream().map(EffectToApply::getUuid).toList());
                        }
                        case FRENZY -> {
                            System.out.println("Résolution d'un choix d'attaque multiple lié au mot-clé FRENZY");
                            ((FrenzyAttackChoice) choice).resolve(game, Boolean.TRUE);
                        }
                        case TARGET -> {
                            System.out.println("Résolution d'un choix de cibles");
                            TargetChoice targetChoice = (TargetChoice) choice;

                            List<UUID> uuids = targetChoice.getAvailableTargets().stream()
                                    .map(CardInstance::getUuid).toList()
                                    .subList(0, targetChoice.getTargetsCount());
                            System.out.printf("Cibles choisies : %s\n", uuids);

                            targetChoice.resolve(game, uuids);
                        }
                        case BOOLEAN -> {
                            System.out.println("Résolution d'un choix booléen");
                            ((BooleanChoice) choice).resolve(game, Boolean.TRUE);
                        }
                    }
                }
            case "stop", "exit":
                return false;
            case "sumUp":
                for (Player player : game.getPlayers()) {
                    sumUpPlayer(player);
                    System.out.println("\n=========================================\n");
                }
                break;
            case "details":
                for (Player player : game.getPlayers()) {
                    detailedSumUpPlayer(player);
                    System.out.println("\n=========================================\n");
                }
                break;
            default:
                System.err.println("Action invalide ; actions possibles : sumUp, details, stop, exit, j, a, rc");
        }

        return true;
    }

    private static void sumUpPlayer(Player player) {
        System.out.printf("%s (%d PV) a : \n", player.getName(), player.getTeam().getLifePoints());
        System.out.printf("\t- %d cartes dans sa main", player.getHand().size());
        System.out.printf("\t- %d cartes sur son terrain\n", player.getBoard().size());
        System.out.printf("\t- %d cartes dans sa pioche", player.getDrawPile().size());
        System.out.printf("\t- %d cartes dans sa défausse\n", player.getDiscardPile().size());
    }

    private static void detailedSumUpPlayer(Player player) {
        System.out.printf("%s (%d PV) : \n", player.getName(), player.getTeam().getLifePoints());
        System.out.println("Main : ");
        for (CardInstance card : player.getHand()) {
            System.out.printf("   - %s (%d) : %s \n", card.getCard().getName(), card.getPower(), card.getKeywords().toString());
        }
        System.out.println("Terrain : ");
        for (CardInstance card : player.getBoard()) {
            System.out.printf("   - %s (%d) : %s \n", card.getCard().getName(), card.getPower(), card.getKeywords().toString());
        }
        System.out.println("Défausse : ");
        for (CardInstance card : player.getDiscardPile()) {
            System.out.printf("   - %s (%d) : %s \n", card.getCard().getName(), card.getPower(), card.getKeywords().toString());
        }

        System.out.printf("Pioche : %d carte(s) restante(s)", player.getDrawPile().size());
    }
}
