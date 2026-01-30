package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that copies the effect of another card
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CopyEffect extends GenericEffect {
    public static final String TYPE = "COPY";

    /**
     * The kind of effect that can be copied
     */
    private EffectTiming timing;
}
