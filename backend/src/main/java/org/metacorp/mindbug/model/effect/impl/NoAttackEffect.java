package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.GenericEffect;

/**
 * Effect that forbids one or more cards to attack
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NoAttackEffect extends GenericEffect {
    public static final String TYPE = "NO_ATTACK";

    /**
     * Should the lowest power creatures
     */
    private boolean lowest;
    /**
     * Should only the cards having this keyword be unable to attack
     */
    private CardKeyword keyword;
}
