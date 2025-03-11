package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that allows current player to draw one or more cards from the draw or discard pile
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DrawEffect extends GenericEffect {
    public final static String TYPE = "DRAW";

    private int value;              // The number of cards to draw
}
