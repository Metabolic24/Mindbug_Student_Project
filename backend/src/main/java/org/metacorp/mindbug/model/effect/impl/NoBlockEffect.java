package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that forbids one or more cards to block
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NoBlockEffect extends GenericEffect {
    public static final String TYPE = "NO_BLOCK";

    /**
     * The number of cards that will be unable to block, -1 for all
     */
    private int value;
    /**
     * The minimum power for cards that will be unable to block
     */
    private Integer max;
    /**
     * The maximum power for cards that will be unable to block
     */
    private Integer min;
    /**
     * Should the highest creatures be unable to block
     */
    private boolean highest;
    /**
     * Should only the cards having this keyword be unable to block
     */
    private CardKeyword keyword;
}
