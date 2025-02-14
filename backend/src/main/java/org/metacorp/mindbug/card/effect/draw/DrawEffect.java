package org.metacorp.mindbug.card.effect.draw;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.game.Game;

/** Effect that allows current player to draw one or more cards from the draw or discard pile */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DrawEffect extends AbstractEffect {
    public final static String TYPE = "DRAW";

    private int value;              // The number of cards to draw

    @Override
    public void apply(Game game, CardInstance card) {
        card.getOwner().drawX(value);
    }
}
