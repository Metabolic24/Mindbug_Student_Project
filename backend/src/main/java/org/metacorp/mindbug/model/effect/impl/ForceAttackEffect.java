package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.effect.GenericEffect;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ForceAttackEffect extends GenericEffect {
    public static final String TYPE = "FORCE_ATTACK";

    private CardKeyword keyword;    // only cards with this keyword are affected
    private boolean singleTarget;   // attack target can only be the effect source card
}
