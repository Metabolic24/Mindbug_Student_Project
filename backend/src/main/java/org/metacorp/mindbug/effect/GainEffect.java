package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.Effect;

/** Effect that increase or modify current player life points */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GainEffect extends Effect {
    public final static String TYPE = "GAIN";

    private int value;      // The number of Life Points that will be gained
    private boolean equal;  // Should life points be set to the opponent ones value

    @Override
    public String getType() {
        return TYPE;
    }
}
