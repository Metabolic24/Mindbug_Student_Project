package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.*;

/** Effect that decrease or modify current player life points */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InflictEffect extends Effect {
    public final static String TYPE = "INFLICT";

    private int value;          // The number of life points to be lost
    private boolean self;       // Should the life points be lost by the current player
    private boolean allButOne;  // Should all life points be lost but one

    @Override
    public String getType() {
        return TYPE;
    }
}
