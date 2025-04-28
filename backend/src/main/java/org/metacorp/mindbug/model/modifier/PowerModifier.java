package org.metacorp.mindbug.model.modifier;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PowerModifier extends AbstractModifier<Integer>{

    public PowerModifier(Integer value) {
        super();
        this.setType(AttackModifierType.POWER);
        this.setValue(value);
    }
}
