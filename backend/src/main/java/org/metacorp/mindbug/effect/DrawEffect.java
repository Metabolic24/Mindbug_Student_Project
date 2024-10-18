package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.*;

/** Effect that allows current player to draw one or more cards from the draw or discard pile */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DrawEffect extends Effect {
    public final static String TYPE = "DRAW";

    private int value;              // The number of cards to draw
    private boolean selfDiscard;    // Should player draw all his discard pile

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player cardOwner = card.getOwner();

        if (selfDiscard) {
            cardOwner.getHand().addAll(cardOwner.getDiscardPile());
            cardOwner.getDiscardPile().clear();
        } else {
            cardOwner.drawX(value);
        }
    }
}
