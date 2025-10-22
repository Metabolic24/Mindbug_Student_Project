package org.metacorp.mindbug.model.modifier;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.metacorp.mindbug.model.card.CardInstance;

/**
 * Modifier that updates card power
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PowerModifier extends AbstractModifier<Integer> {

    public PowerModifier(Integer value) {
        this.setValue(value);
    }

    @Override
    public void apply(CardInstance cardInstance) {
        cardInstance.changePower(this.getValue());
    }
}
