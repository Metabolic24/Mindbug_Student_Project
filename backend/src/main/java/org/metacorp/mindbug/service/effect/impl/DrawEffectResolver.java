package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DrawEffect;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

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
        game.getLogger().debug("Player {} draws {} cards due to {} effect", getLoggablePlayer(effectSource.getOwner()), effect.getValue(), getLoggableCard(effectSource));

        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}
