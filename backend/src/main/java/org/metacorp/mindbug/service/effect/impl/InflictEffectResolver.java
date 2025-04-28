package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.model.player.Team;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;

/**
 * Effect resolver for InflictEffect
 */
public class InflictEffectResolver extends GenericEffectResolver<InflictEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public InflictEffectResolver(InflictEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        int value = effect.getValue();
        boolean self = effect.isSelf();
        boolean allButOne = effect.isAllButOne();

        Player affectedPlayer = self ? card.getOwner() : card.getOwner().getOpponent(game.getPlayers());
        Team affectedTeam = affectedPlayer.getTeam();

        if (allButOne) {
            if (affectedTeam.getLifePoints() > 1) {
                affectedTeam.setLifePoints(1);
                GameService.lifePointLost(affectedPlayer, game);
            }
        } else {
            affectedTeam.loseLifePoints(value);
            GameService.lifePointLost(affectedPlayer, game);
        }
    }
}
