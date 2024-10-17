package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.Effect;

/** Effect that may discard one or more cards from opponent hand */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DiscardEffect extends Effect {
    public final static String TYPE = "DISCARD";

    private int value; // The number of cards to be discarded

    @Override
    public String getType() {
        return TYPE;
    }
}
