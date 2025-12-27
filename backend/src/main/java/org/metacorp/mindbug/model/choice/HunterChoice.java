package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.ChoiceUtils;

import java.util.Set;
import java.util.UUID;

@Data
public class HunterChoice implements IChoice<UUID> {
    @NonNull
    // The player that has to select target
    private Player playerToChoose;

    @NonNull
    // The card which is attacking
    private CardInstance attackingCard;

    // All the available targets
    @NonNull
    private Set<CardInstance> availableTargets;

    @Override
    public void resolve(UUID chosenTargetId, Game game) throws GameStateException {
        ChoiceUtils.resolveHunterChoice(chosenTargetId, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.HUNTER;
    }
}
