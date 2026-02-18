package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.ChoiceUtils;

import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class SimultaneousEffectsChoice extends AbstractChoice<UUID> {
    @NonNull
    private Set<EffectsToApply> effectsToSort;

    /**
     * Constructor
     *
     * @param effectsToSort the simultaneous effects list
     * @param currentPlayer the current player
     */
    public SimultaneousEffectsChoice(@NonNull Set<EffectsToApply> effectsToSort, @NonNull Player currentPlayer) {
        this.playerToChoose = currentPlayer;
        this.effectsToSort = effectsToSort;
    }

    @Override
    public void resolve(UUID cardId, Game game) throws GameStateException {
        ChoiceUtils.resolveSimultaneousChoice(cardId, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.SIMULTANEOUS;
    }
}
