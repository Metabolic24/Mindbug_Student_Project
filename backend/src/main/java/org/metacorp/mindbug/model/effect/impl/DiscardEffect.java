package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that may discard one or more cards from opponent hand
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DiscardEffect extends GenericEffect {
    public final static String TYPE = "DISCARD";

    private int value; // The number of cards to be discarded
}
