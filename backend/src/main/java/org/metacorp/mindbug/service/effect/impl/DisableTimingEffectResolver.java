package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DisableTimingEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Effect resolver for DisableTimingEffect
 */
public class DisableTimingEffectResolver extends EffectResolver<DisableTimingEffect> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public DisableTimingEffectResolver(DisableTimingEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        Player opponentPlayer = effectSource.getOwner().getOpponent(game.getPlayers());
        opponentPlayer.disableTiming(effect.getValue());

        game.getLogger().debug("{} effects disabled for player {} due to {} effect", effect.getValue(), getLoggablePlayer(opponentPlayer), getLoggableCard(effectSource));
        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}
