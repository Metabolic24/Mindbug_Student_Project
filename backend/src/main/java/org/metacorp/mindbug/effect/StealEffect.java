package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.Effect;

/** Effect that steals card from the opponent hand or board depending on several conditions */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class StealEffect extends Effect {
    public final static String TYPE = "STEAL";

    private int value;              // The number of cards to steal
    private Integer min;            // The minimum power for card(s) to be stolen
    private Integer max;            // The maximum power for card(s) to be stolen

    private StealSource source;     // From where should card(s) be stolen

    private boolean mustPlay;       // Should stolen card(s) be played in this effect resolution
    private boolean mayPlay;        // May the stolen card(s) be played in this effect resolution
    private boolean random;         // Should stolen card(s) be chosen randomly

    @Override
    public String getType() {
        return TYPE;
    }
}
