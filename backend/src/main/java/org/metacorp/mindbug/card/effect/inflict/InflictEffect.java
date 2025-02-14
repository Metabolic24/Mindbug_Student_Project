package org.metacorp.mindbug.card.effect.inflict;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.game.Game;
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
    public void apply(Game game, CardInstance card) {
        Player affectedPlayer = self ? card.getOwner() : card.getOwner().getOpponent(game.getPlayers());
        Team affectedTeam = affectedPlayer.getTeam();

        if (allButOne) {
            if (affectedTeam.getLifePoints() > 1) {
                affectedTeam.setLifePoints(1);
                game.lifePointLost(affectedPlayer);
            }
        } else {
            affectedTeam.loseLifePoints(value);
            game.lifePointLost(affectedPlayer);
        }
    }
}
