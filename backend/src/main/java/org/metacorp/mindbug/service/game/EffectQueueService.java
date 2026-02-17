package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.exception.EffectQueueStopException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.effect.Effect;
import org.metacorp.mindbug.model.effect.EffectLocation;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectType;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.GenericEffect;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.WebSocketService;
import org.metacorp.mindbug.service.effect.EffectResolver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service that fills and processes the effect queue
 */
public class EffectQueueService {

    // Not to be used
    private EffectQueueService() {
        // Nothing to do
    }

    /**
     * Add card effect(s) to the effect queue if allowed
     *
     * @param card        the card containing the effects to be added
     * @param timing      the effect timing to use to add effects
     * @param effectQueue the effect queue
     */
    public static void addBoardEffectsToQueue(CardInstance card, EffectTiming timing, EffectQueue effectQueue) {
        addEffectsToQueue(card, timing, effectQueue, EffectLocation.BOARD);
    }

    /**
     * Add card effect(s) to the effect queue if allowed
     *
     * @param card        the card containing the effects to be added
     * @param timing      the effect timing to use to add effects
     * @param effectQueue the effect queue
     */
    public static void addDiscardEffectsToQueue(CardInstance card, EffectTiming timing, EffectQueue effectQueue) {
        addEffectsToQueue(card, timing, effectQueue, EffectLocation.DISCARD);
    }

    /**
     * Add card effect(s) to the effect queue if allowed
     *
     * @param card        the card containing the effects to be added
     * @param timing      the effect timing to use to add effects
     * @param effectQueue the effect queue
     * @param location    the effect location
     */
    private static void addEffectsToQueue(CardInstance card, EffectTiming timing, EffectQueue effectQueue, EffectLocation location) {
        List<Effect> effects = card.getEffects(timing).stream()
                .filter(effect -> effect.getLocation() == location).collect(Collectors.toList());
        if (!effects.isEmpty()) {
            if (card.getOwner().canTrigger(timing)) {
                effectQueue.add(new EffectsToApply(effects, card, timing));
            } else {
                System.out.printf("Effets %s annulés\n", timing);
            }
        }
    }

    /**
     * Resolve effect queue
     *
     * @param fromSimultaneousChoice is this method called after a simultaneous choice resolution
     * @param game                   the current game state
     * @throws GameStateException if a game state error is detected during effect queue resolution
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    public static void resolveEffectQueue(boolean fromSimultaneousChoice, Game game) throws GameStateException, WebSocketException {
        // If the game is over, immediately stop the effect queue resolution
        if (game.isFinished()) {
            return;
        }

        // Check that there is no pending choice
        if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before resolving effect queue", Map.of("choice", game.getChoice()));
        }

        EffectQueue effectQueue = game.getEffectQueue();

        // Specific behavior if there are more than one effect to apply in the queue
        if (!fromSimultaneousChoice && !effectQueue.isResolvingEffect() && effectQueue.size() >= 2) {
            createSimultaneousChoice(game);
            return;
        }

        // Reset this value as it would block effect processing
        effectQueue.setResolvingEffect(false);

        // Loop over each effect
        while (!effectQueue.isEmpty()) {
            EffectsToApply currentEffect = effectQueue.peek();

            try {
                // Process any cost effect(s) first
                if (currentEffect.hasCost()) {
                    processEffects(currentEffect.getCost().iterator(), game, currentEffect);
                }

                // Process the main effects of the EffectsToApply instance
                processEffects(currentEffect.getEffects().iterator(), game, currentEffect);

                // Only remove effect from queue after it is applied to avoid loss of data (should not be applied if a CostEffect has just been resolved)
                effectQueue.remove(currentEffect);

                // If there are many remaining effects, and we are not resolving (cost) effects, then create a simultaneous choice
                if (!effectQueue.isResolvingEffect() && effectQueue.size() >= 2) {
                    createSimultaneousChoice(game);
                    return;
                }
            } catch (EffectQueueStopException e) {
                // Just exit from the current method
                return;
            }

            // Reset this value as it would block the next effect(s) processing
            effectQueue.setResolvingEffect(false);
        }

        // Execute the after effect when queue is empty
        game.runAfterEffect();
    }

    /**
     * Process a list of effects
     *
     * @param iterator      the iterator on the effect list
     * @param game          the current game state
     * @param currentEffect the EffectsToApply that is currently processed
     * @throws EffectQueueStopException if the game is finished or if a choice has been created while trying to resolve an effect
     * @throws WebSocketException       if an error occurred while sending game event through WebSocket
     */
    private static void processEffects(Iterator<? extends Effect> iterator, Game game, EffectsToApply currentEffect)
            throws GameStateException, EffectQueueStopException, WebSocketException {
        // Initialize a boolean value that will be true if a CostEffect is resolved so a new EffectsToApply is added to the effect queue
        EffectQueue effectQueue = game.getEffectQueue();

        // Iterate while there are remaining effects and only if a (cost) effect is being resolved
        while (iterator.hasNext() && !effectQueue.isResolvingEffect()) {
            // Get the next effect, apply it, then remove it from the list
            Effect effect = iterator.next();

            EffectResolver.getResolver((GenericEffect) effect, currentEffect.getCard()).apply(game, currentEffect.getTiming());
            // The cost effect resolution may change isResolvingEffect value to true if no choice is created

            // Stop the process if the game is finished
            if (game.isFinished()) {
                throw new EffectQueueStopException();
            }

            // Specific behavior for non COST effect
            if (effect.getType() != EffectType.COST) {
                GameStateService.refreshGameState(game);

                // Send a WS event only if the effect resolution did not create a choice
                if (game.getChoice() == null) {
                    WebSocketService.sendGameEvent(WsGameEventType.EFFECT_RESOLVED, game);
                }
            }

            // Remove the current effect from the queue in any case
            iterator.remove();

            if (game.getChoice() != null) {
                // If there are no more effects in the list and there is no effects remaining in the EffectsToApply (eq. the current list is not the cost for other effects), then remove the whole EffectsToApply
                if (!iterator.hasNext() && currentEffect.getEffects().isEmpty()) {
                    effectQueue.remove(currentEffect);
                } else if (iterator.hasNext()) {
                    // Update this boolean attribute so next effect resolution won't trigger a simultaneous choice first
                    effectQueue.setResolvingEffect(true);
                }

                // Send update through WebSocket
                WebSocketService.sendGameEvent(WsGameEventType.CHOICE, game);
                HistoryService.logChoice(game);

                // Raise an exception as we don't want to process the effect queue while a choice is pending
                throw new EffectQueueStopException();
            }
        }
    }

    /**
     * Create a simultaneous choice using the effects contained in the effect queue
     *
     * @param game the current game state
     * @throws WebSocketException if an error occurred while sending game event through WebSocket
     */
    private static void createSimultaneousChoice(Game game) throws WebSocketException {
        EffectQueue effectQueue = game.getEffectQueue();

        game.setChoice(new SimultaneousEffectsChoice(new HashSet<>(effectQueue), game.getCurrentPlayer()));
        effectQueue.clear();

        // Send update through WebSocket
        WebSocketService.sendGameEvent(WsGameEventType.CHOICE, game);
        HistoryService.logChoice(game);
    }
}
