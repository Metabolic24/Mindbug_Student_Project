package org.metacorp.mindbug.model.modifier;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.model.card.CardInstance;

/**
 * Modifier that disables blocking ability
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BlockModifier extends AbstractModifier<Void> {

    @Override
    public void apply(CardInstance cardInstance) {
        cardInstance.setAbleToBlock(false);
    }
}
