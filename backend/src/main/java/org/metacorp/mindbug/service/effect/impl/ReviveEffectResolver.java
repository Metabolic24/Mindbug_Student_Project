package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.ReviveEffect;
import org.metacorp.mindbug.service.EffectQueueService;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

/**
 * Effect resolver for ReviveEffect
 */
public class ReviveEffectResolver extends GenericEffectResolver<ReviveEffect> implements ResolvableEffect<Boolean> {

    private CardInstance card;

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public ReviveEffectResolver(ReviveEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card) {
        this.card = card;
        game.setChoice(new BooleanChoice(card.getOwner(), card, this));
    }

    @Override
    public void resolve(Game game, Boolean choice) {
        if (choice != null && choice) {
            card.getOwner().getDiscardPile().remove(card);
            card.getOwner().getBoard().add(card);

            EffectQueueService.addBoardEffectsToQueue(card, EffectTiming.PLAY, game.getEffectQueue());
        }
    }
}