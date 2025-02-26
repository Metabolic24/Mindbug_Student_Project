package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.impl.DrawEffect;
import org.metacorp.mindbug.service.effect.AbstractEffectResolver;

/**
 * Effect resolver for DrawEffect
 */
public class DrawEffectResolver extends AbstractEffectResolver<DrawEffect> {

    /**
     * Constructor
     * @param effect the effect to be resolved
     */
    public DrawEffectResolver(DrawEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card) {
        card.getOwner().drawX(effect.getValue());
    }
}