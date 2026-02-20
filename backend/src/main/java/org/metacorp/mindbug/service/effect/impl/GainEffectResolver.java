package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.model.player.Team;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.game.GameStateService;
import java.util.*;
/**
 * Effect resolver for GainEffect
 */
public class GainEffectResolver extends EffectResolver<GainEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public GainEffectResolver(GainEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        this.effectSource = card;

        int value = effect.getValue();
        boolean equal = effect.isEqual();

        Player cardOwner = card.getOwner();
        Team team = cardOwner.getTeam();

        if (equal) {
            int oldLifePoints = team.getLifePoints();
            
            //get all opponents
            List<Player> opponents = cardOwner.getOpponent(game.getPlayers());
            
            // get the life oppenents
            team.setLifePoints(opponents.getFirst().getTeam().getLifePoints());

            if (oldLifePoints > team.getLifePoints()) {
                GameStateService.lifePointLost(cardOwner, game);
            }
        } else {
            team.gainLifePoints(value);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, null);
    }
}
