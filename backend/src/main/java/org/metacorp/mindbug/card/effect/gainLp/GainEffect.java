package org.metacorp.mindbug.card.effect.gainLp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.player.Team;

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
               game.lifePointLost(cardOwner);
            }
        } else {
            team.gainLifePoints(value);
        }
    }
}
