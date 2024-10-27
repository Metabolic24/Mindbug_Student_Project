package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.CardInstance;
import org.metacorp.mindbug.Effect;
import org.metacorp.mindbug.Game;

@Data
@EqualsAndHashCode(callSuper = true)
public class InternalEffect extends Effect {
    @NonNull
    private Runnable runnableAction;

    @Override
    public String getType() {
        return "INTERNAL";
    }

    @Override
    public void apply(Game game, CardInstance card) {
        runnableAction.run();
    }
}
