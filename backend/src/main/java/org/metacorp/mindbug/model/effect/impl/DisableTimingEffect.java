package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that may disable a specific timing type of effects
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DisableTimingEffect extends GenericEffect {
    public static final String TYPE = "DISABLE_TIMING";

    private EffectTiming value; // The timing of effects that should be disabled
}
