package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.Effect;

/** Effect that increase the power of one or more cards depending on several conditions */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PowerUpEffect extends Effect {
    public final static String TYPE = "POWER_UP";

    private int value;          // The power that should be gained by the card(s)
    private Integer lifePoints; // The required amount of life points so the power gain is applied
    private boolean alone;      // Should the power gain be applied only if card is alone
    private boolean byEnemy;    // Should power be gained for each opponent card
    private boolean allies;     // Should allies be affected or not
    private boolean self;       // Should the current card be affected or not
    private boolean selfTurn;   // Should the power gain be only available on the owner turn

    @Override
    public String getType() {
        return TYPE;
    }
}
