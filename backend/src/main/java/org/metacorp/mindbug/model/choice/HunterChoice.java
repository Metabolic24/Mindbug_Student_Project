package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.utils.ChoiceUtils;

import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class HunterChoice extends AbstractChoice<UUID> {
    @NonNull
    // The card which is attacking
    private CardInstance attackingCard;

    // All the available targets
    @NonNull
    private Set<CardInstance> availableTargets;

    /**
     * Constructor
     * @param attackingCard the attacking card
     * @param availableTargets the list of available attack targets
     */
    public HunterChoice(@NonNull CardInstance attackingCard, @NonNull Set<CardInstance> availableTargets) {
        this.attackingCard = attackingCard;
        this.playerToChoose = attackingCard.getOwner();
        this.availableTargets = availableTargets;
    }

    @Override
    public void resolve(UUID chosenTargetId, Game game) throws GameStateException, WebSocketException {
        ChoiceUtils.resolveHunterChoice(chosenTargetId, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.HUNTER;
    }
}
