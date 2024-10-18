package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Effect;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

/** Effect that forbids one or more cards to block */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NoBlockEffect extends Effect{
    public final static String TYPE = "NO_BLOCK";

    private int value;          // The number of cards that will be unable to block, -1 for all
    private Integer max;        // The maximum power for cards that will be unable to block
    private boolean highest;    // Should the highest creatures be unable to block

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player opponent = card.getOwner().getOpponent(game.getPlayers());

        if (highest) {
            for (CardInstance highestCard : opponent.getHighestCards()) {
                highestCard.setCanBlock(false);
            }
        } else if (max != null) {
            for (CardInstance opponentCard : opponent.getBoard()) {
                if (opponentCard.getPower() <= max) {
                    opponentCard.setCanBlock(false);
                }
            }
        } else {
            //TODO Implement choice
        }
    }
}
