package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.utils.ChoiceUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class TargetChoice implements IChoice<List<UUID>> {
    @NonNull
    // The player that has to select targets
    private Player playerToChoose;

    @NonNull
    // The card which effect required this choice creation
    private CardInstance effectSource;

    @NonNull
    // The effect that should be resolved after the choice resolution
    private ResolvableEffect<List<CardInstance>> effect;

    // How many card should the player choose, -1 means he must select all cards
    @NonNull
    private Integer targetsCount;

    // All the available targets
    @NonNull
    private Set<CardInstance> availableTargets;

    @Override
    public void resolve(List<UUID> chosenTargetIds, Game game) {
        ChoiceUtils.resolveTargetChoice(chosenTargetIds, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.TARGET;
    }
}
