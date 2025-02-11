package org.metacorp.mindbug;

import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.Random;

public class AutoApp {

    private static final Random random = new Random();

    public static void main(String[] args) {
        Game game = AppUtils.startGame();

        AppUtils.runAndCheckErrors(game, () -> {
            do {
                resolveTurn(game);
            } while (!game.isFinished());
        });
    }

    public static void resolveTurn(Game game) {
        Player currentPlayer = game.getCurrentPlayer();

        if (game.getCurrentChoice() != null) {
            AppUtils.resolveChoice(game);
        } else if (currentPlayer.getBoard().isEmpty()) {
            if (currentPlayer.getHand().isEmpty()) {
                game.endGame(currentPlayer);
            } else {
                AppUtils.play(game);
            }
        } else {
            if (!currentPlayer.getHand().isEmpty() && random.nextBoolean()) {
                AppUtils.play(game);
            } else {
                AppUtils.attack(game);
            }
        }

        while (game.getCurrentChoice() != null) {
            AppUtils.resolveChoice(game);
        }
    }
}
