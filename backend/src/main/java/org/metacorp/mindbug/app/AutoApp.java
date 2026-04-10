package org.metacorp.mindbug.app;

import org.metacorp.mindbug.exception.CardSetException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.AiPlayer;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.AiUtils;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.Random;

/**
 * Mindbug application that launches a game then uses random choices to reach the end of it
 */
public class AutoApp {

    private static final Random RND = new Random();

    static void main(String[] args) throws CardSetException {
        start(AppUtils.createGame(args, true));
    }

    /**
     * Start the given game <br>
     * Separated method to ease unit testing
     *
     * @param game the game to start
     */
    public static void start(Game game) {
        AppUtils.runAndCheckErrors(game, () -> {
            do {
                resolveTurn(game);
            } while (!game.isFinished());
        });
    }

    /**
     * Resolve a game turn
     *
     * @param game the current game
     * @throws GameStateException if the game reaches an inconsistant state
     */
    private static void resolveTurn(Game game) throws GameStateException, WebSocketException {
        Player currentPlayer = game.getCurrentPlayer();

        // Here we consider that the current player can at least play or attack
        boolean canPlay = !currentPlayer.getHand().isEmpty();
        boolean canAttack = currentPlayer.getBoard().stream().anyMatch(CardInstance::isAbleToAttack);

        if (canAttack && (!canPlay || RND.nextBoolean())) {
            // Declare attack
            AppUtils.declareAttack(game);
        } else {
            // Play a card
            AppUtils.play(game);
        }

        resolveChoices(game);

        if (!game.isFinished()) {
            AppUtils.nextTurn(game);
        }
    }

    /**
     * Resolve zero, one or multiple choices
     *
     * @param game the current game
     */
    private static void resolveChoices(Game game) throws GameStateException, WebSocketException {
        while (game.getChoice() != null && !game.isFinished()) {
            AiUtils.resolveChoice(game, (AiPlayer) game.getChoice().getPlayerToChoose());
        }
    }
}
