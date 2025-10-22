package org.metacorp.mindbug.model.modifier;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.model.card.CardInstance;

/**
 * Abstract class that describes a modifier.
 * A modifier is a temporary bonus or malus related to a specific card.
 * Currently, modifiers are designed to be cleared at the end of the player turn.
 *
 * @param <T> the type of the modifier value
 */
@Getter
@Setter
public abstract class AbstractModifier<T> {
    private T value;

    public abstract void apply(CardInstance cardInstance);
}
