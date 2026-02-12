package org.metacorp.mindbug.model.modifier;

import org.metacorp.mindbug.model.card.CardInstance;

/**
 * Modifier that adds a protection
 */
public class ProtectionModifier extends AbstractModifier<Void> {

    @Override
    public void apply(CardInstance cardInstance) {
        cardInstance.setProtection(true);
    }
}
