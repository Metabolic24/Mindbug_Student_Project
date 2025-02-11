package org.metacorp.mindbug.card.effect.noAttack;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.player.Player;

/** Effect that forbids one or more cards to attack */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NoAttackEffect extends AbstractEffect {
    public final static String TYPE = "NO_ATTACK";

    private boolean lowest; // Should the lowest power creatures be unable to attack

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        if (lowest) {
            Player opponent = card.getOwner().getOpponent(game.getPlayers());
            for (CardInstance lowestCard : opponent.getLowestCards()) {
                lowestCard.setCanAttack(false);
            }
        }
    }
}
