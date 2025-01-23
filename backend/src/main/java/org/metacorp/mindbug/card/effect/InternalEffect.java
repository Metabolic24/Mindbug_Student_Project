package org.metacorp.mindbug.card.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.Game;

@Data
@EqualsAndHashCode(callSuper = true)
public class InternalEffect extends AbstractEffect {
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
