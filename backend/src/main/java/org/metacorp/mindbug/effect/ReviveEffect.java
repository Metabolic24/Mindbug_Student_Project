package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Effect;
import org.metacorp.mindbug.Game;

/**
 * Effect that revives the current card on some specific conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ReviveEffect extends Effect {
    public final static String TYPE = "REVIVE";

    private boolean loseLife; // Should card revive when losing life points

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        if (loseLife) {
            // TODO Implement choice
        }
    }
}
