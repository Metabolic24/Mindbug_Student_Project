package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that decrease or modify current player life points
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InflictEffect extends GenericEffect {
    public final static String TYPE = "INFLICT";

    /**
     * The number of life points to be lost
     */
    private int value;
    /**
     * Should the life points be lost by the current player
     */
    private boolean self;
    /**
     * Should all life points be lost but one
     */
    private boolean allButOne;
    /**
     * Should the amount of life points be equal to the mindbug count of the opponent
     */
    private boolean mindbugCount;
}
