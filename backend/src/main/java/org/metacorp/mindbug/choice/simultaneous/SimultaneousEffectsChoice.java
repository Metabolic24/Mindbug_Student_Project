package org.metacorp.mindbug.choice.simultaneous;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.effect.EffectToApply;
import org.metacorp.mindbug.choice.ChoiceType;
import org.metacorp.mindbug.choice.IChoice;
import org.metacorp.mindbug.player.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class SimultaneousEffectsChoice implements IChoice<List<UUID>> {

    @NonNull
    private Player playerToChoose;      // The player that must make the choice

    @NonNull
    private Set<EffectToApply> effectsToSort;

    @Override
    public void resolve(Game game, List<UUID> sortedEffectIds) {
        if (sortedEffectIds == null || sortedEffectIds.size() != this.effectsToSort.size()) {
            //TODO Raise an error
            return;
        }

        for (UUID effectId : sortedEffectIds) {
            EffectToApply foundEffect = effectsToSort.stream()
                    .filter(effectToApply -> effectToApply.getUuid().equals(effectId))
                    .findFirst().orElseThrow();

            game.getEffectQueue().add(foundEffect);
        }

        // Reset the choice only if the given choice list was valid
        game.resetChoice();
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.SIMULTANEOUS;
    }
}
