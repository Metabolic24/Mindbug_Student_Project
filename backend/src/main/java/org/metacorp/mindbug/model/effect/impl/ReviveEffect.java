package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that revives the current card on some specific conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ReviveEffect extends GenericEffect {
    public static final String TYPE = "REVIVE";
}
