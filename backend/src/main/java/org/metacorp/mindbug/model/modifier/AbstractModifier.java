package org.metacorp.mindbug.model.modifier;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractModifier<T> {
    private AttackModifierType type;
    private T value;

    protected AbstractModifier() {

    }
}
