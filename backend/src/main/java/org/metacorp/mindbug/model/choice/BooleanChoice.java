package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.utils.ChoiceUtils;

@Data
public class BooleanChoice implements IChoice<Boolean> {

    @NonNull
    private Player playerToChoose;

    @NonNull
    private CardInstance card;

    @NonNull
    private ResolvableEffect<Boolean> effectResolver;

    @Override
    public void resolve(Boolean choice, Game game) {
        ChoiceUtils.resolveBooleanChoice(choice, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.BOOLEAN;
    }
}
