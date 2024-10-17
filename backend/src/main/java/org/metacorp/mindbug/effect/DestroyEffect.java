package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.Effect;

/** Effect that may destroy one or more cards */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DestroyEffect extends Effect {
    public final static String TYPE = "DESTROY";

    private Integer value;          // The number of cards to destroy, -1 if all cards should be destroyed
    private boolean lessAllies;     // Effect is active if player has less allies than the opponent
    private boolean lowest;         // Destroy the card(s) with lowest power
    private boolean selfAllowed;    // Can this effect affect current player cards
    private Integer min;            // The minimum power of card(s) to be destroyed
    private Integer max;            // The maximum power of card(s) to be destroyed

    @Override
    public String getType() {
        return TYPE;
    }
}
