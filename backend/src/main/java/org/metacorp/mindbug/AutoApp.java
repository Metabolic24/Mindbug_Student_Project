package org.metacorp.mindbug;

import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.List;
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

        if (game.getChoice() != null) {
            AppUtils.resolveChoice(game);
        } else if (game.getAttackingCard() != null ) {
            AppUtils.frenzyAttack(game);
        } else {
            List<CardInstance> availableCards = currentPlayer.getBoard().stream().filter(CardInstance::isCanAttack).toList();
            if (availableCards.isEmpty()) {
                if (currentPlayer.getHand().isEmpty()) {
                    game.endGame(currentPlayer);
                    return;
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
        }

        while (game.getChoice() != null && !game.isFinished()) {
            AppUtils.resolveChoice(game);
        }
    }
}
