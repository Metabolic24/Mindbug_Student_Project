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

@EqualsAndHashCode(callSuper = true)
@Data
public class BooleanChoice extends AbstractChoice<Boolean> {

    @NonNull
    private CardInstance sourceCard;

    @NonNull
    private ResolvableEffect<Boolean> effectResolver;

    private CardInstance card;

    /**
     * Constructor
     *
     * @param playerToChoose the player to choose
     * @param sourceCard     the choice source card
     * @param effectResolver the effect resolver to be triggered when choice is resolved
     */
    public BooleanChoice(@NonNull Player playerToChoose, @NonNull CardInstance sourceCard, @NonNull ResolvableEffect<Boolean> effectResolver) {
        this(playerToChoose, sourceCard, effectResolver, null);
    }

    /**
     * Constructor
     *
     * @param playerToChoose the player to choose
     * @param sourceCard     the choice source card
     * @param effectResolver the effect resolver to be triggered when choice is resolved
     * @param card           the target card of the effect resolution
     */
    public BooleanChoice(@NonNull Player playerToChoose, @NonNull CardInstance sourceCard, @NonNull ResolvableEffect<Boolean> effectResolver, CardInstance card) {
        this.playerToChoose = playerToChoose;
        this.sourceCard = sourceCard;
        this.effectResolver = effectResolver;
        this.card = card;
    }

    @Override
    public void resolve(Boolean choice, Game game) throws GameStateException, WebSocketException {
        ChoiceUtils.resolveBooleanChoice(choice, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.BOOLEAN;
    }
}
