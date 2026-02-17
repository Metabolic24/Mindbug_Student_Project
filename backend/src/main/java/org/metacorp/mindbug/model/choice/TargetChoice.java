package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.utils.ChoiceUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class TargetChoice extends AbstractChoice<List<UUID>> {

    // The card which effect required this choice creation
    @NonNull
    private CardInstance effectSource;

    // The effect that should be resolved after the choice resolution
    @NonNull
    private ResolvableEffect<List<CardInstance>> effect;

    // How many card should the player choose, -1 means he must select all cards
    @NonNull
    private Integer targetsCount;

    // All the available targets
    @NonNull
    private Set<CardInstance> availableTargets;

    private boolean optional;

    public TargetChoice(@NonNull Player playerToChoose, @NonNull CardInstance effectSource, @NonNull ResolvableEffect<List<CardInstance>> effect, @NonNull Integer targetsCount, @NonNull Set<CardInstance> availableTargets) {
        this.playerToChoose = playerToChoose;
        this.effectSource = effectSource;
        this.effect = effect;
        this.targetsCount = targetsCount;
        this.availableTargets = availableTargets;
    }

    @Override
    public void resolve(List<UUID> chosenTargetIds, Game game) throws GameStateException, WebSocketException {
        ChoiceUtils.resolveTargetChoice(chosenTargetIds, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.TARGET;
    }
}
