package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that sends one or more cards back to the hand of a player (aka bounce effect)
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class BounceEffect extends GenericEffect {
    public final static String TYPE = "BOUNCE";

    /**
     * The number of cards to bounce, -1 if all cards should be bounced
     */
    private Integer value;
}

