package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.effect.AbstractEffect;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.model.player.Team;

/** Effect that increase or modify current player life points */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GainEffect extends AbstractEffect {
    public final static String TYPE = "GAIN";

    private int value;      // The number of Life Points that will be gained
    private boolean equal;  // Should life points be set to the opponent ones value

    @Override
    public void apply(Game game, CardInstance card) {
        Player cardOwner = card.getOwner();
        Team team = cardOwner.getTeam();

        if (equal) {
            int oldLifePoints = team.getLifePoints();

            Player opponent = cardOwner.getOpponent(game.getPlayers());
            team.setLifePoints(opponent.getTeam().getLifePoints());

            if (oldLifePoints > team.getLifePoints()) {
               GameService.lifePointLost(cardOwner, game);
            }
        } else {
            team.gainLifePoints(value);
        }
    }
}
