package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that increase the power of one or more cards depending on several conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PowerUpEffect extends GenericEffect {
    public final static String TYPE = "POWER_UP";

    /**
     * The power that should be gained by the card(s)
     */
    private int value;
    /**
     * Should the current card be affected or not (default: true)
     */
    private boolean self = true;
    /**
     * Should allies be affected or not
     */
    private boolean allies;
    /**
     * The required amount of life points so the power gain is applied
     */
    private Integer lifePoints;
    /**
     * Should the power gain be applied only if card is alone
     */
    private boolean alone;
    /**
     * Should power be gained for each opponent card
     */
    private boolean byEnemy;
    /**
     * Should the power gain be only available on the owner turn
     */
    private boolean selfTurn;

    @Override
    public int getPriority() {
        return 0;
    }
}
