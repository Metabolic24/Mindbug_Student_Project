package org.metacorp.mindbug.service;

import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.AbstractEffect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.choice.SimultaneousEffectsChoice;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.service.effect.AbstractEffectResolver;

import java.util.*;

public class EffectQueueService {

    // Not to be used
    private EffectQueueService() {
        // Nothing to do
    }

    public static void addEffectsToQueue(CardInstance card, EffectTiming timing, Queue<EffectsToApply> effectQueue) {
        List<AbstractEffect> effects = card.getEffects(timing);
        if (!effects.isEmpty()) {
            if (card.getOwner().canTrigger(timing)) {
                effectQueue.add(new EffectsToApply(effects, card));
            } else {
                System.out.printf("Effets %s annulés\n", timing);
                //TODO Voir s'il faut faire quelque chose à ce moment-là
            }
        }
    }

    public static void resolveEffectQueue(boolean fromSimultaneousChoice, Game game) throws GameStateException {
        if (game.getChoice() != null) {
            throw new GameStateException("a choice needs to be resolved before resolving effect queue", Map.of("choice", game.getChoice()));
        }

        Queue<EffectsToApply> effectQueue = game.getEffectQueue();

        // Specific behavior if there are more than one effect to apply in the queue
        if (!fromSimultaneousChoice && effectQueue.size() >= 2) {
            game.setChoice(new SimultaneousEffectsChoice(new HashSet<>(effectQueue)));
            effectQueue.clear();
            return;
        }

        // Loop over each effect
        while (!effectQueue.isEmpty()) {
            EffectsToApply currentEffect = effectQueue.peek();

            Iterator<AbstractEffect> iterator = currentEffect.getEffects().iterator();
            while (iterator.hasNext()) {
                // Get the next effect, apply it then remove it from the list
                AbstractEffect effect = iterator.next();
                AbstractEffectResolver.getResolver(effect).apply(game, currentEffect.getCard());
                iterator.remove();

                GameService.refreshGameState(game);

                // Stop the process if game is finished
                if (game.isFinished()) { //TODO A voir si on gère ça avec une exception
                    return;
                }

                if (game.getChoice() != null) {
                    if (!iterator.hasNext()) {
                        effectQueue.remove(currentEffect);
                    }
                    return;
                }
            }

            // Only remove effect from queue after it is applied to avoid loss of data
            effectQueue.remove(currentEffect); // TODO Faut-il envisager un système transactionnel ou similaire pour gérer le fait qu'une application d'effet puisse échouer sans pour autant dégrader l'état actuel du jeu?

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
