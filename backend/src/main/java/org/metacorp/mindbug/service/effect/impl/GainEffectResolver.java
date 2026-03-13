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

import java.util.List;
/**
 * Effect resolver for GainEffect
 */
public class GainEffectResolver extends EffectResolver<GainEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public GainEffectResolver(GainEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) throws WebSocketException{
        this.effectSource = card;

        int value = effect.getValue();
        boolean equal = effect.isEqual();

        Player cardOwner = card.getOwner();
        Team team = cardOwner.getTeam();

        int oldLifePoints = team.getLifePoints();

        if (equal) {
           
            
            //get all opponents
            List<Player> opponents = cardOwner.getOpponents(game.getPlayers());
            
            // get the life oppenents
            team.setLifePoints(opponents.getFirst().getTeam().getLifePoints());

            if (oldLifePoints > team.getLifePoints()) {
                GameStateService.lifePointLost(cardOwner, game);
            }
        } else {
            team.gainLifePoints(value);
        }
         game.getLogger().debug("{} LP changed ({} -> {}) due to {} effect",
                getLoggablePlayer(cardOwner), oldLifePoints, team.getLifePoints(), getLoggableCard(effectSource));
        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}
