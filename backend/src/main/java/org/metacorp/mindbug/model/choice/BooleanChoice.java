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

@Data
@EqualsAndHashCode(callSuper = false)
public class BooleanChoice extends AbstractChoice<Boolean> {
    @NonNull
    private CardInstance sourceCard;

    @NonNull
    private ResolvableEffect<Boolean> effectResolver;

    private CardInstance card;

    public BooleanChoice(Player playerToChoose, CardInstance sourceCard, ResolvableEffect<Boolean> effectResolver) {
        this.playerToChoose = playerToChoose;
        this.sourceCard = sourceCard;
        this.effectResolver = effectResolver;
    }

    public BooleanChoice(Player playerToChoose, CardInstance sourceCard, ResolvableEffect<Boolean> effectResolver, CardInstance card) {
        this(playerToChoose, sourceCard, effectResolver);
        this.card = card;
    }

    @Override
    public void resolve(Boolean choice, Game game) {
        ChoiceUtils.resolveBooleanChoice(choice, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.BOOLEAN;
    }
}
