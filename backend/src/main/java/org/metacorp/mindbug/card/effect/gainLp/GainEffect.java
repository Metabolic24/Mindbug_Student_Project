package org.metacorp.mindbug.card.effect.gainLp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.player.Player;

/** Effect that increase or modify current player life points */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GainEffect extends AbstractEffect {
    public final static String TYPE = "GAIN";

    private int value;      // The number of Life Points that will be gained
    private boolean equal;  // Should life points be set to the opponent ones value

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player cardOwner = card.getOwner();

        if (equal) {
            Player opponent = cardOwner.getOpponent(game.getPlayers());
            cardOwner.getTeam().setLifePoints(opponent.getTeam().getLifePoints());
        } else {
            cardOwner.getTeam().gainLifePoints(value);
        }
    }
}
