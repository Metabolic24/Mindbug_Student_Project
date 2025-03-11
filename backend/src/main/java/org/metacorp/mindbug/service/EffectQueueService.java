package org.metacorp.mindbug.service;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.model.effect.*;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        List<GenericEffect> effects = card.getEffects(timing).stream()
                .filter(genericEffect -> genericEffect.getLocation() == location).collect(Collectors.toList());
        if (!effects.isEmpty()) {
            if (card.getOwner().canTrigger(timing)) {
                effectQueue.add(new EffectsToApply(effects, card));
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
        if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before resolving effect queue", Map.of("choice", game.getChoice()));
        }

        EffectQueue effectQueue = game.getEffectQueue();

        // Specific behavior if there are more than one effect to apply in the queue
        if (!fromSimultaneousChoice && !effectQueue.isResolvingEffect() && effectQueue.size() >= 2) {
            game.setChoice(new SimultaneousEffectsChoice(new HashSet<>(effectQueue)));
            effectQueue.clear();
            return;
        }

        // Reset this value as it is no more needed
        effectQueue.setResolvingEffect(false);

        // Loop over each effect
        while (!effectQueue.isEmpty()) {
            EffectsToApply currentEffect = effectQueue.peek();

            Iterator<GenericEffect> iterator = currentEffect.getEffects().iterator();
            while (iterator.hasNext()) {
                // Get the next effect, apply it then remove it from the list
                GenericEffect effect = iterator.next();
                GenericEffectResolver.getResolver(effect).apply(game, currentEffect.getCard());
                iterator.remove();

                GameService.refreshGameState(game);

                // Stop the process if game is finished
                if (game.isFinished()) { //TODO A voir si on gère ça avec une exception
                    return;
                }

                if (game.getChoice() != null) {
                    if (!iterator.hasNext()) {
                        effectQueue.remove(currentEffect);

                    } else {
                        // Update this boolean attribute so next effect resolution won't trigger a simultaneous choice first
                        effectQueue.setResolvingEffect(true);
                    }
                    return;
                }
            }

            // Only remove effect from queue after it is applied to avoid loss of data
            effectQueue.remove(currentEffect);

            // If there is many remaining effects, then create a simultaneous choice
            if (effectQueue.size() >= 2) {
                game.setChoice(new SimultaneousEffectsChoice(new HashSet<>(effectQueue)));
                effectQueue.clear();
                return;
            }
        }

        // Execute the after effect when queue is empty
        game.runAfterEffect();
    }
}
