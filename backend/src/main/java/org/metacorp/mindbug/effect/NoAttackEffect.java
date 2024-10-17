package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.Effect;

/** Effect that forbids one or more cards to attack */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class NoAttackEffect extends Effect {
    public final static String TYPE = "NO_ATTACK";

    private boolean lowest; // Should the lowest power creatures be unable to attack

    @Override
    public String getType() {
        return TYPE;
    }
}
