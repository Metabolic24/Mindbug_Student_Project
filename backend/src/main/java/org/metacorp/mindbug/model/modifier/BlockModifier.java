package org.metacorp.mindbug.model.modifier;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BlockModifier extends AbstractModifier<Void>{

    public BlockModifier() {
        super();
        this.setType(AttackModifierType.BLOCK);
    }
}
