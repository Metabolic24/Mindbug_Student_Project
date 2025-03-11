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

    private int value;          // The number of life points to be lost
    private boolean self;       // Should the life points be lost by the current player
    private boolean allButOne;  // Should all life points be lost but one
}
