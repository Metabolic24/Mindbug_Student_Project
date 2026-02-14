package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DrawEffect;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;

/**
 * Effect resolver for DrawEffect
 */
public class DrawEffectResolver extends EffectResolver<DrawEffect> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public DrawEffectResolver(DrawEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        effectSource.getOwner().drawX(effect.getValue());
        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}
