package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Effect;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.Player;

/** Effect that may discard one or more cards from opponent hand */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DiscardEffect extends Effect {
    public final static String TYPE = "DISCARD";

    private int value; // The number of cards to be discarded

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player opponent = card.getOwner().getOpponent(game.getPlayers());

        if (opponent.getHand().size() <= value) {
            opponent.getDiscardPile().addAll(opponent.getHand());
            opponent.getHand().clear();
        } else {
            // TODO Implement choices
        }
    }
}
