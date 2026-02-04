package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ProtectionEffect extends GenericEffect {
    public static final String TYPE = "PROTECTION";

    /**
     * Should this effect affects the card itself
     */
    private boolean self;

    /**
     * Should this effect affects allies
     */
    private boolean allies;
}
