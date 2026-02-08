package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.history.HistoryKey;
import org.metacorp.mindbug.service.HistoryService;

import java.util.Map;

public class ActionService {

    // Not to be used
    private ActionService() {
        // Nothing to do
    }

    /**
     * Method executed when a player plays a card, no matter how or why
     *
     * @param card the card whose action should be resolved
     * @param game the current game state
     * @throws GameStateException if game state appears to be inconsistent before processing
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void resolveAction(CardInstance card, Game game) throws GameStateException, WebSocketException {
        if (game.getPlayedCard() != null) {
            throw new GameStateException("a played card needs to be resolved before attacking",
                    Map.of("playedCard", game.getPlayedCard()));
        } else if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before picking a new card",
                    Map.of("choice", game.getChoice()));
        } else if (game.getAttackingCard() != null) {
            throw new GameStateException("an attack needs to be resolved before picking a new card",
                    Map.of("attackingCard", game.getAttackingCard()));
        }

        if (card.getEffects(EffectTiming.ACTION).isEmpty()) {
            throw new GameStateException("Chosen card has no ACTION effect", Map.of("card", card));
        }

        // Add all the ACTION effects to the queue (if any), if player is allowed to trigger them
        EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.ACTION, game.getEffectQueue());
        HistoryService.log(game, HistoryKey.ACTION, card);

        // Set afterEffect so it starts a new turn when effect is resolved
        game.setAfterEffect(() -> GameStateService.newTurn(game));

        // Resolve the effect queue so ACTION effects will be resolved then afterEffect executed
        EffectQueueService.resolveEffectQueue(false, game);
    }
}
