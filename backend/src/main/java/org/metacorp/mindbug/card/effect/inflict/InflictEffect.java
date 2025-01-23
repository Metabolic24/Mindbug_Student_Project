package org.metacorp.mindbug.card.effect.inflict;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.*;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.player.Team;

/** Effect that decrease or modify current player life points */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InflictEffect extends AbstractEffect {
    public final static String TYPE = "INFLICT";

    private int value;          // The number of life points to be lost
    private boolean self;       // Should the life points be lost by the current player
    private boolean allButOne;  // Should all life points be lost but one

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player cardOwner = card.getOwner();

        Team teamToDecrease = self ? cardOwner.getTeam() :
                cardOwner.getOpponent(game.getPlayers()).getTeam();

        if (allButOne) {
            if (teamToDecrease.getLifePoints() > 1) {
                teamToDecrease.setLifePoints(1);
                game.lifePointLost(cardOwner);
            }
        } else {
            teamToDecrease.loseLifePoints(value);
            game.lifePointLost(cardOwner);
        }
    }
}
