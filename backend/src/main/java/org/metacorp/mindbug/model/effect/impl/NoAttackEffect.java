package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

/** Effect that forbids one or more cards to attack */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NoAttackEffect extends GenericEffect {
    public final static String TYPE = "NO_ATTACK";

    private boolean lowest; // Should the lowest power creatures be unable to attack
}
