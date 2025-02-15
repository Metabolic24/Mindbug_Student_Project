package org.metacorp.mindbug.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.effect.ResolvableEffect;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.player.Player;

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
        if (chosenTargetIds == null || chosenTargetIds.size() != targetsCount) {
            //TODO Raise an error or log message
            return;
        }

        List<CardInstance> chosenTargets = availableTargets.stream().filter(target -> chosenTargetIds.contains(target.getUuid())).toList();
        if (chosenTargets.size() != targetsCount) {
            //TODO Raise an error or log message
        }

        effect.resolve(game, chosenTargets);

        // Reset the choice only if the given choice list was valid
        game.setChoice(null);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.TARGET;
    }
}
