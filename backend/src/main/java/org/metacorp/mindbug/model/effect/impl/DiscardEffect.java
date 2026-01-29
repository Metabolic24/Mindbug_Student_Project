package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that may discard one or more cards from opponent hand
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DiscardEffect extends GenericEffect {
    public final static String TYPE = "DISCARD";

    /** The number of cards to be discarded */
    private int value;
    /** If true, a card is discarded for each enemy card on board */
    private boolean eachEnemy;
    /** Should the card(s) be discarded by the source card owner */
    private boolean self;
    /** Should the cards be discarded from the draw pile */
    private boolean drawPile;

}
