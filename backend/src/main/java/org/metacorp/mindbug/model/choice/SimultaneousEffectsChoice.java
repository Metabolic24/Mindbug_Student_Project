package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.ChoiceUtils;

import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class SimultaneousEffectsChoice extends AbstractChoice<UUID> {
    @NonNull
    private Set<EffectsToApply> effectsToSort;

    public SimultaneousEffectsChoice(Player playerToChoose, Set<EffectsToApply> effectsToSort) {
        this.playerToChoose = playerToChoose;
        this.effectsToSort = effectsToSort;
    }

    @Override
    public void resolve(UUID cardId, Game game) {
        ChoiceUtils.resolveSimultaneousChoice(cardId, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.SIMULTANEOUS;
    }
}
