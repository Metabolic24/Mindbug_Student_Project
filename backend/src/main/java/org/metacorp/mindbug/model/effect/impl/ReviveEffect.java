package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.AbstractEffect;

/**
 * Effect that revives the current card on some specific conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ReviveEffect extends AbstractEffect {
    public final static String TYPE = "REVIVE";
}
