package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.BooleanChoice;
import org.metacorp.mindbug.model.effect.CostEffect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.impl.CopyEffect;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

/**
 * Effect resolver for CostEffect
 */
public class CostEffectResolver extends EffectResolver<CostEffect> implements ResolvableEffect<Boolean> {

    private EffectTiming timing;

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
     public CostEffectResolver(CostEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, CardInstance sourceCard, EffectTiming timing) {
        this.effectSource = sourceCard;
        this.timing = timing;

        if (effect.isOptional()) {
             game.getLogger().debug("Player {} must decide to resolve or not {} COST effect",
                    getLoggablePlayer(effectSource.getOwner()), getLoggableCard(effectSource));
            game.setChoice(new BooleanChoice(sourceCard.getOwner(), sourceCard, this));
            game.getLogger().debug("Player {} must choose an effect to copy (available targets : {})",
                        getLoggablePlayer(sourceOwner), getLoggableCards(availableCards));
        } else {
            resolve(game);
        }
    }

    @Override
    public void resolve(Game game, Boolean choice) {
        if (choice != null && choice) {
            resolve(game);
        }
    }

    private void resolve(Game game) {
        EffectsToApply costEffectToApply = new EffectsToApply(effect.getCost(), effect.getEffects(), effectSource, timing);
        game.getEffectQueue().push(costEffectToApply);
        game.getEffectQueue().setResolvingEffect(true);
        game.getLogger().debug("COST effect of {} is about to be resolved", getLoggableCard(effectSource));
    }
}
