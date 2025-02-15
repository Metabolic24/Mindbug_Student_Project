package org.metacorp.mindbug.service;

import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Utility service that updates game state when a card is picked or played
 */
public class PlayCardService {

    // Not to be used
    private PlayCardService() {
        // Nothing to do
    }

    /**
     * Method executed when a player choose a card that he would like to play
     * @param card the picked card
     * @param game the current game state
     * @throws GameStateException if game state appears to be inconsistent before processing
     */
    public static void pickCard(CardInstance card, Game game) throws GameStateException {
        if (game.getPlayedCard() != null) {
            throw new GameStateException("a card has already been picked", Map.of("playedCard", game.getPlayedCard()));
        } else if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before picking a new card", Map.of("choice", game.getChoice()));
        } else if (game.getAttackingCard() != null) {
            throw new GameStateException("an attack needs to be resolved before picking a new card", Map.of("attackingCard", game.getAttackingCard()));
        }

        // Update current player hand
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayer.getHand().remove(card);
        currentPlayer.refillHand();

        // Update game state
        game.setPlayedCard(card);

        Player opponent = game.getOpponent();
        if (opponent.getMindBugs() == 0) {
            playCard(game);
        }
    }

    /**
     * Method executed when a player plays a card, no matter how or why
     * @param game the current game state
     * @throws GameStateException if game state appears to be inconsistent before processing
     */
    public static void playCard(Game game) throws GameStateException {
        playCard(null, game);
    }

    /**
     * Method executed when a player plays a card, no matter how or why
     * @param mindbugger the player that used a mindbug for this card (may be null)
     * @param game the current game state
     * @throws GameStateException if game state appears to be inconsistent before processing
     */
    public static void playCard(Player mindbugger, Game game) throws GameStateException {
        if (game.getPlayedCard() == null) {
            throw new GameStateException("no card has been picked");
        } else if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before picking a new card", Map.of("choice", game.getChoice()));
        } else if (game.getAttackingCard() != null) {
            throw new GameStateException("an attack needs to be resolved before picking a new card", Map.of("attackingCard", game.getAttackingCard()));
        }

        // Update the owner if card has been mindbugged
        if (mindbugger != null && !mindbugger.hasMindbug()) {
            throw new GameStateException(MessageFormat.format("player {0} has no mindbug left", mindbugger.getName()));
        }

        managePlayedCard(mindbugger, game);

        // Resolve the effect queue so PLAY effects will be resolved then afterEffect executed
        EffectQueueService.resolveEffectQueue(false, game);
    }

    /**
     * Update game state after a card is played
     * @param mindbugger the player that used a mindbug for this card (may be null)
     * @param game the current game state
     */
    protected static void managePlayedCard(Player mindbugger, Game game) {
        CardInstance playedCard = game.getPlayedCard();

        // Specific behavior if card has been mindbugged
        if (mindbugger != null) {
            playedCard.setOwner(mindbugger);
            mindbugger.useMindbug();
        }

        // Add card to its owner board then refresh game state
        playedCard.getOwner().getBoard().add(playedCard);
        GameService.refreshGameState(game);

        // Add PLAY effects (if any) if player is allowed to trigger them
        EffectQueueService.addEffectsToQueue(playedCard, EffectTiming.PLAY, game.getEffectQueue());

        game.setAfterEffect(() -> {
            // Reset the played card value
            game.setPlayedCard(null);

            // Only trigger next turn if no mindbug has been used
            if (mindbugger == null) {
                game.setCurrentPlayer(game.getOpponent());
            }
        });
    }
}
