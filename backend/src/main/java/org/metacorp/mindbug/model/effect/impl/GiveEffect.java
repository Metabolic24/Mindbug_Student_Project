package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that gives control of a creature to the opponent
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GiveEffect extends GenericEffect {
    public final static String TYPE = "GIVE";

    private boolean itself = true;
}
