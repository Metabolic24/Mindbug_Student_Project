package org.metacorp.mindbug;

import org.metacorp.mindbug.choice.Choice;

import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Game game = new Game("Player1", "Player2");

        System.out.println("Début du jeu !!!");

        for(Player player : game.getPlayers()) {
            System.out.printf("\n%s : Main\n", player.getName());
            for(CardInstance card : player.getHand()) {
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
            case "p":
                System.out.printf("%s joue la carte '%s'\n", currentPlayer.getName(), currentPlayer.getHand().getFirst().getCard().getName());
                game.pickCard(currentPlayer.getHand().getFirst());
                game.playCard(currentPlayer.getHand().getFirst(), false);
                break;
            case "a":
                System.out.printf("%s attaque avec la carte '%s'\n", currentPlayer.getName(), currentPlayer.getBoard().getFirst().getCard().getName());

                Player opponentPlayer = currentPlayer.getOpponent(game.getPlayers());
                if (opponentPlayer.getBoard().isEmpty()) {
                    System.out.printf("%s ne peut pas défendre\n", opponentPlayer.getName());
                    game.attack(currentPlayer.getBoard().getFirst(), null, opponentPlayer);
                } else {
                    System.out.printf("%s défend avec la carte '%s'\n", opponentPlayer.getName(), opponentPlayer.getBoard().getFirst().getCard().getName());
                    game.attack(currentPlayer.getBoard().getFirst(), opponentPlayer.getBoard().getFirst(), opponentPlayer);
                }

                break;
            case "rcs":
                if (game.getChoice() == null) {
                    System.err.println("Action invalide");
                } else {
                    System.out.printf("Ordre choisi : %s\n", game.getChoice());

                    game.applyChoice(game.getChoice());
                }
                break;
            case "rc":
                if (game.getChoiceList() == null) {
                    System.err.println("Action invalide");
                } else {
                    List<Choice> choiceList = game.getChoiceList().getChoices().subList(0, game.getChoiceList().getChoicesCount());

                    System.out.printf("Cartes choisies : %s\n", choiceList);

                    game.resolveChoiceList(choiceList);
                }
                break;
            case "stop","exit":
                return false;
            case "show":
                for(Player player : game.getPlayers()) {
                    System.out.printf("Résumé %s : %d PV\n", player.getName(), player.getTeam().getLifePoints());

                    System.out.println("Main");
                    for(CardInstance card : player.getHand()) {
                        System.out.printf("   - %s (%d) : %s \n", card.getCard().getName(), card.getPower(), card.getKeywords().toString());
                    }

                    System.out.println("Terrain");
                    for(CardInstance card : player.getBoard()) {
                        System.out.printf("   - %s (%d) : %s \n", card.getCard().getName(), card.getPower(), card.getKeywords().toString());
                    }

                    System.out.println("Défausse");
                    for(CardInstance card : player.getDiscardPile()) {
                        System.out.printf("   - %s\n", card.getCard().getName());
                    }
                }
                break;
            default:
                System.err.println("Action invalide ; actions possibles : show, exit, p, a, rcs, rc");
        }

        return true;
    }

    private static void sumUpPlayer(Player player) {
        System.out.println("%s a %d cartes dans sa main\n");
        System.out.println("%s a %d cartes dans sa défausse\n");
        System.out.println("%s a %d cartes sur son terrain\n");
    }
}
