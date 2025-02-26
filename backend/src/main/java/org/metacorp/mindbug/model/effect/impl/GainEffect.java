package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.AbstractEffect;

/** Effect that increase or modify current player life points */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GainEffect extends AbstractEffect {
    public final static String TYPE = "GAIN";

    private int value;      // The number of Life Points that will be gained
    private boolean equal;  // Should life points be set to the opponent ones value
}
