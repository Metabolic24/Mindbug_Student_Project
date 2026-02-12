package org.metacorp.mindbug.app;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.PlayerService;
import org.metacorp.mindbug.utils.AiUtils;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.List;
import java.util.Random;

/**
 * Mindbug application that launches a game then uses random choices to reach the end of it
 */
public class AutoApp2v2 {

    private static final Random RND = new Random();

    public static void main() {
        PlayerService playerService = new PlayerService();
        Game game = AppUtils.start2v2Game(playerService);

        AppUtils.runAndCheckErrors(game, () -> {
            do {// do it one time
                resolveTurn(game); 
            } while (!game.isFinished());//repeat while
        });
    }

    /**
     * Resolve a game turn
     *
     * @param game the current game
     * @throws GameStateException if the game reaches an inconsistant state
     */
    private static void resolveTurn(Game game) throws GameStateException {
        Player currentPlayer = game.getCurrentPlayer();
        List<CardInstance> availableCards = currentPlayer.getBoard().stream().filter(CardInstance::isAbleToAttack).toList();

        boolean attack = currentPlayer.getHand().isEmpty() || (!availableCards.isEmpty() && RND.nextBoolean());
        if (attack) {
            // Declare attack
            AppUtils.declareAttack(game);
            resolveChoices(game);

            // Resolve attack or Frenzy case
            while (game.getAttackingCard() != null && !game.isFinished()) {
                AppUtils.resolveAttack(game);
                resolveChoices(game);
            }
        } else {
            // Play a card
            AppUtils.play(game);
            resolveChoices(game);
        }

        if (!game.isFinished()) {
            AppUtils.nextTurn(game);
        }
    }

    /**
     * Resolve zero, one or multiple choices
     *
     * @param game the current game
     */
    private static void resolveChoices(Game game) throws GameStateException {
        while (game.getChoice() != null && !game.isFinished()) {
            AiUtils.resolveChoice(game);
        }
    }
}
