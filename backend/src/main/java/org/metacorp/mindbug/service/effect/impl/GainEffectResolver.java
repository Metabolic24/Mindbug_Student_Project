package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.model.player.Team;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.game.GameStateService;

import static org.metacorp.mindbug.utils.LogUtils.getLoggableCard;
import static org.metacorp.mindbug.utils.LogUtils.getLoggablePlayer;

/**
 * Effect resolver for GainEffect
 */
public class GainEffectResolver extends EffectResolver<GainEffect> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public GainEffectResolver(GainEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) throws WebSocketException {
        int value = effect.getValue();
        boolean equal = effect.isEqual();

        Player cardOwner = effectSource.getOwner();
        Team team = cardOwner.getTeam();

        int oldLifePoints = team.getLifePoints();

        if (equal) {
            Player opponent = cardOwner.getOpponent(game.getPlayers());
            team.setLifePoints(opponent.getTeam().getLifePoints());

            if (oldLifePoints > team.getLifePoints()) {
                GameStateService.lifePointLost(cardOwner, game);
            }
        } else {
            team.gainLifePoints(value);
        }

        game.getLogger().debug("{} LP changed ({} -> {}) due to {} effect", getLoggablePlayer(cardOwner), oldLifePoints, team.getLifePoints(), getLoggableCard(effectSource));

        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}
