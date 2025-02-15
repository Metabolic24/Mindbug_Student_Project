package org.metacorp.mindbug.app;

import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.model.player.Player;
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

    public static void resolveTurn(Game game) throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();

        if (game.getChoice() != null) {
            AppUtils.resolveChoice(game);
        } else if (game.getAttackingCard() != null ) {
            AppUtils.frenzyAttack(game);
        } else {
            List<CardInstance> availableCards = currentPlayer.getBoard().stream().filter(CardInstance::isAbleToAttack).toList();
            if (availableCards.isEmpty()) {
                if (currentPlayer.getHand().isEmpty()) {
                    GameService.endGame(currentPlayer, game);
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
