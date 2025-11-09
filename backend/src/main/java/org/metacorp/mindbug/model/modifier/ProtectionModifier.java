package org.metacorp.mindbug.model.modifier;

import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;

/**
 * Modifier that adds a protection
 */
public class ProtectionModifier extends AbstractModifier<Void> {

    @Override
    public void apply(CardInstance cardInstance) {
        cardInstance.setProtection(true);
    }
}
