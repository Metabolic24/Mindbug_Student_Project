package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.*;

/** Effect that may disable a specific timing type of effects */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DisableTimingEffect extends Effect {
    public final static String TYPE = "DISABLE_TIMING";

    private EffectTiming value; // The timing of effects that should be disabled

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        card.getOwner().getOpponent(game.getPlayers()).disableTiming(value);
    }
}
