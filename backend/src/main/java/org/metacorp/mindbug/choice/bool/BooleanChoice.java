package org.metacorp.mindbug.choice.bool;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.ResolvableEffect;
import org.metacorp.mindbug.choice.ChoiceType;
import org.metacorp.mindbug.choice.IChoice;
import org.metacorp.mindbug.player.Player;

@Data
public class BooleanChoice implements IChoice<Boolean> {

    @NonNull
    private Player playerToChoose;

    @NonNull
    private CardInstance card;

    @NonNull
    private ResolvableEffect<BooleanChoiceResolver> effect;

    @Override
    public void resolve(Game game, Boolean choiceResolver) {
        if (choiceResolver != null) {
            effect.resolve(game, new BooleanChoiceResolver(playerToChoose, card, choiceResolver));
        } else {
            //TODO Manage error
        }
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.BOOLEAN;
    }
}
