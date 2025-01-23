package org.metacorp.mindbug.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.card.effect.EffectTiming;
import org.metacorp.mindbug.player.Player;

import java.util.ArrayList;

/**
 * Choice that happens when multiple effects trigger in the same timing
 *  Player has to order in which order he wants to resolve the effects
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SimultaneousChoice extends ArrayList<Choice> {
    @NonNull
    private Player playerToChoose;      // The player that must make the choice
    @NonNull
    private EffectTiming effectTiming;
}

