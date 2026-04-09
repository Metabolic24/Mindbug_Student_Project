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

@EqualsAndHashCode(callSuper = true)
@Data
public class PlayerChoice extends AbstractChoice<Player> {

    // The card which effect required this choice creation
    @NonNull
    private CardInstance effectSource;

    // The effect that should be resolved after the choice resolution
    @NonNull
    private ResolvableEffect<?> effect;

    // The list of available players
    @NonNull
    private List<Player> availableTargets;

    public PlayerChoice(@NonNull Player playerToChoose, @NonNull CardInstance effectSource, @NonNull ResolvableEffect<?> effect,
                        @NonNull List<Player> availableTargets) {
        this.playerToChoose = playerToChoose;
        this.effectSource = effectSource;
        this.effect = effect;
        this.availableTargets = availableTargets;
    }

    @Override
    public void resolve(Player chosenTargetIds, Game game) throws GameStateException, WebSocketException {
        ChoiceUtils.resolvePlayerChoice(chosenTargetIds, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.PLAYER;
    }
}
