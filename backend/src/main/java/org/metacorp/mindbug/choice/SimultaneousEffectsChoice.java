package org.metacorp.mindbug.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.Game;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class SimultaneousEffectsChoice implements IChoice<List<UUID>> {

    @NonNull
    private Set<EffectsToApply> effectsToSort;

    @Override
    public void resolve(List<UUID> sortedEffectIds, Game game) {
        if (sortedEffectIds == null || sortedEffectIds.size() != this.effectsToSort.size()) {
            //TODO Raise an error
            return;
        }

        for (UUID effectId : sortedEffectIds) {
            EffectsToApply foundEffect = effectsToSort.stream()
                    .filter(effectToApply -> effectToApply.getUuid().equals(effectId))
                    .findFirst().orElseThrow();

            game.getEffectQueue().add(foundEffect);
        }

        // Reset the choice only if the given choice list was valid
        game.setChoice(null);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.SIMULTANEOUS;
    }
}
