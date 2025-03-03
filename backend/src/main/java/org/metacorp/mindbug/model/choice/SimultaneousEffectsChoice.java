package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.utils.ChoiceUtils;

import java.util.Set;
import java.util.UUID;

@Data
public class SimultaneousEffectsChoice implements IChoice<UUID> {
    @NonNull
    private Set<EffectsToApply> effectsToSort;

    @Override
    public void resolve(UUID cardId, Game game) {
        ChoiceUtils.resolveSimultaneousChoice(cardId, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.SIMULTANEOUS;
    }
}
