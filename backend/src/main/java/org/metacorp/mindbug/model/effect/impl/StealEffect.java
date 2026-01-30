package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;
import org.metacorp.mindbug.model.effect.steal.StealSource;
import org.metacorp.mindbug.model.effect.steal.StealTargetSelection;

/**
 * Effect that steals card from the opponent hand or board depending on several conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class StealEffect extends GenericEffect {
    public static final String TYPE = "STEAL";

    private int value;                          // The number of cards to steal (-1 <=> all cards)
    private boolean optional;                   // Is this effect optional?
    private Integer min;                        // The minimum power for card(s) to be stolen
    private Integer max;                        // The maximum power for card(s) to be stolen
    private StealTargetSelection selection;     // How targets to be stolen are selected (SELF, OPPONENT, RANDOM; default : SELF)
    private StealSource source;                 // From where should card(s) be stolen

    private boolean mustPlay;                   // Should stolen card(s) be played in this effect resolution?
    private boolean mayPlay;                    // May the stolen card(s) be played in this effect resolution?
}
