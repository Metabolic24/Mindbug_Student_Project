package org.metacorp.mindbug.service.game;

import org.metacorp.mindbug.dto.ws.WsGameEventType;
import org.metacorp.mindbug.exception.EffectQueueStopException;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.effect.CostEffect;
import org.metacorp.mindbug.model.effect.Effect;
import org.metacorp.mindbug.model.effect.EffectLocation;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.GenericEffect;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.WebSocketService;
import org.metacorp.mindbug.service.effect.impl.CostEffectResolver;
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
     * Resolve effect queue<br>
     *
     * @param fromSimultaneousChoice is this method called after a simultaneous choice resolution
     * @param game                   the current game state
     * @throws GameStateException if a game state error is detected during effect queue resolution
     */
    public static void resolveEffectQueue(boolean fromSimultaneousChoice, Game game) throws GameStateException {
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
            game.setChoice(new SimultaneousEffectsChoice(new HashSet<>(effectQueue)));
            effectQueue.clear();

            // Send update through WebSocket
            WebSocketService.sendGameEvent(WsGameEventType.CHOICE, game);
            HistoryService.logChoice(game);

            return;
        }

        // Reset this value as it is no more necessary
        effectQueue.setResolvingEffect(false);

        // Loop over each effect
        while (!effectQueue.isEmpty()) {
            EffectsToApply currentEffect = effectQueue.peek();

            try {
                if (currentEffect.getCost() != null && !currentEffect.getCost().isEmpty()) {
                    processEffects(currentEffect.getCost().iterator(), game, currentEffect);
                }

                boolean costResolved = processEffects(currentEffect.getEffects().iterator(), game, currentEffect);
                if (!costResolved) {
                    // Only remove effect from queue after it is applied to avoid loss of data (should not be applied if a CostEffect has just been resolved
                    effectQueue.remove(currentEffect);

                    // If there is many remaining effects, then create a simultaneous choice
                    if (effectQueue.size() >= 2) {
                        game.setChoice(new SimultaneousEffectsChoice(new HashSet<>(effectQueue)));
                        effectQueue.clear();

                        WebSocketService.sendGameEvent(WsGameEventType.CHOICE, game);
                        HistoryService.logChoice(game);

                        return;
                    }
                }
            } catch (EffectQueueStopException e) {
                // Just exit from the current method
                return;
            }
        }

        // Execute the after effect when queue is empty
        game.runAfterEffect();
    }

    private static boolean processEffects(Iterator<? extends Effect> iterator, Game game, EffectsToApply currentEffect) throws EffectQueueStopException {
        EffectQueue effectQueue = game.getEffectQueue();

        boolean costResolved = false;

        while (iterator.hasNext() && !costResolved) {
            // Get the next effect, apply it, then remove it from the list
            Effect effect = iterator.next();

            if (effect.hasCost()) {
                new CostEffectResolver((CostEffect) effect).apply(game, currentEffect.getCard(), currentEffect.getTiming());
                costResolved = true;
            } else {
                EffectResolver.getResolver((GenericEffect) effect).apply(game, currentEffect.getCard(), currentEffect.getTiming());

                GameStateService.refreshGameState(game);

                // Stop the process if the game is finished
                if (game.isFinished()) {
                    throw new EffectQueueStopException();
                }

                if (game.getChoice() == null) {
                    WebSocketService.sendGameEvent(WsGameEventType.EFFECT_RESOLVED, game);
                }
            }

            iterator.remove();

            if (game.getChoice() != null) {
                if (!iterator.hasNext()) {
                    effectQueue.remove(currentEffect);
                }

                if (iterator.hasNext() || effect.hasCost()) {
                    // Update this boolean attribute so next effect resolution won't trigger a simultaneous choice first
                    effectQueue.setResolvingEffect(true);
                }

                // Send update through WebSocket
                WebSocketService.sendGameEvent(WsGameEventType.CHOICE, game);
                HistoryService.logChoice(game);

                throw new EffectQueueStopException();
            }
        }

        return costResolved;
    }
}
