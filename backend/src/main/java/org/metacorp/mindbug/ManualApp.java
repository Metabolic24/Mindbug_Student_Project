package org.metacorp.mindbug;

import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.Scanner;

public class ManualApp {

    public static void main(String[] args) {
        Game game = AppUtils.startGame();

        AppUtils.runAndCheckErrors(game, () -> {
            Scanner scanner = new Scanner(System.in);
            boolean ok;

            do {
                ok = processInput(scanner.nextLine(), game);
            } while (ok);
        });
    }

    private static boolean processInput(String input, Game game) {
        String[] tokens = input.split(" ");
        switch (tokens[0].toLowerCase()) {
            case "play", "p":
                AppUtils.play(game);

                while(game.getChoice() != null) {
                    AppUtils.resolveChoice(game);
                }
                break;
            case "attack", "a":
                AppUtils.attack(game);
                while(game.getChoice() != null) {
                    AppUtils.resolveChoice(game);
                }
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
                break;
            case "stop", "exit":
                return false;
            default:
                System.err.println("Action invalide ; actions possibles : play, p, attack, a, sumup, s, details, d, stop, exit");
        }

        return !game.isFinished() && (!game.getCurrentPlayer().getHand().isEmpty() || !game.getCurrentPlayer().getBoard().isEmpty());
    }

    private static void sumUpPlayer(Player player) {
        System.out.printf("%s (%d PV, %d Mindbug(s), Main : %d, Terrain : %d, Défausse : %d, Pioche : %d\n",
                player.getName(), player.getTeam().getLifePoints(), player.getMindBugs(), player.getHand().size(), player.getBoard().size(),
                player.getDiscardPile().size(), player.getDrawPile().size());
    }
}
