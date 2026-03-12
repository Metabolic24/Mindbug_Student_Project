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
public class AutoApp {

    private static final Random RND = new Random();
    private static final String MODE_2V2 = "2v2";

    public static void main() {
        main(new String[0]);
    }

    public static void main(String[] args) {
        PlayerService playerService = new PlayerService();
        boolean is2v2 = false;
        if (args != null && args.length > 0 && args[0] != null) {
            switch (args[0].toLowerCase()) {
                case MODE_2V2 -> is2v2 = true;
                default -> System.out.printf("Mode inconnu '%s', démarrage en 1v1.%n", args[0]);
            }
        }
        Game game = is2v2
                ? AppUtils.start2v2Game(playerService, true)
                : AppUtils.startGame(playerService, true);

        AppUtils.runAndCheckErrors(game, () -> {
            do {// play the turn of the current player 
                resolveTurn(game);
            } while (!game.isFinished());//repeat while the game is not finished
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
