package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.AbstractEffect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.Game;

/** Effect that may disable a specific timing type of effects */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DisableTimingEffect extends AbstractEffect {
    public final static String TYPE = "DISABLE_TIMING";

    private EffectTiming value; // The timing of effects that should be disabled

    @Override
    public void apply(Game game, CardInstance card) {
        card.getOwner().getOpponent(game.getPlayers()).disableTiming(value);
    }
}
