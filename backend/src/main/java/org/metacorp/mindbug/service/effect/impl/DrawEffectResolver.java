package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DrawEffect;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.HistoryService;

/**
 * Effect resolver for DrawEffect
 */
public class DrawEffectResolver extends EffectResolver<DrawEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public DrawEffectResolver(DrawEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        this.effectSource = card;

        card.getOwner().drawX(effect.getValue());
        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}
