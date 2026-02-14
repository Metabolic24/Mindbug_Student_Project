package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.ReviveEffect;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.service.game.EffectQueueService;

/**
 * Effect resolver for ReviveEffect
 */
public class ReviveEffectResolver extends EffectResolver<ReviveEffect> implements ResolvableEffect<Boolean> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public ReviveEffectResolver(ReviveEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        game.setChoice(new BooleanChoice(effectSource.getOwner(), effectSource, this));
    }

    @Override
    public void resolve(Game game, Boolean choice) {
        if (choice != null && choice) {
            effectSource.getOwner().getDiscardPile().remove(effectSource);
            effectSource.getOwner().getBoard().add(effectSource);

            EffectQueueService.addBoardEffectsToQueue(effectSource, EffectTiming.PLAY, game.getEffectQueue());

            HistoryService.logEffect(game, effect.getType(), effectSource, null);
        }
    }
}
