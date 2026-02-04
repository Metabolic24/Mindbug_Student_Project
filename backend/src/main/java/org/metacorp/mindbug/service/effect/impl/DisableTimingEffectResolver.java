package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DisableTimingEffect;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;

/**
 * Effect resolver for DisableTimingEffect
 */
public class DisableTimingEffectResolver extends EffectResolver<DisableTimingEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public DisableTimingEffectResolver(DisableTimingEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        card.getOwner().getOpponent(game.getPlayers()).disableTiming(effect.getValue());

        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}
