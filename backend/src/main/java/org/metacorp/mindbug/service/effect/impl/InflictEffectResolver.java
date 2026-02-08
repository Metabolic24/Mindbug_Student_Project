package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.model.player.Team;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.game.GameStateService;

/**
 * Effect resolver for InflictEffect
 */
public class InflictEffectResolver extends EffectResolver<InflictEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public InflictEffectResolver(InflictEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) throws WebSocketException {
        this.effectSource = card;

        boolean self = effect.isSelf();
        boolean allButOne = effect.isAllButOne();

        Player affectedPlayer = self ? card.getOwner() : card.getOwner().getOpponent(game.getPlayers());
        Team affectedTeam = affectedPlayer.getTeam();

        if (allButOne) {
            if (affectedTeam.getLifePoints() > 1) {
                affectedTeam.setLifePoints(1);
                GameStateService.lifePointLost(affectedPlayer, game);
            }
        } else {
            int value = effect.isMindbugCount() ? affectedPlayer.getMindBugs() : effect.getValue();
            affectedTeam.loseLifePoints(value);
            GameStateService.lifePointLost(affectedPlayer, game);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}
