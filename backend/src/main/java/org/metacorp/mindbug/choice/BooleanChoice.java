package org.metacorp.mindbug.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.effect.ResolvableEffect;
import org.metacorp.mindbug.model.choice.BooleanChoiceResolver;
import org.metacorp.mindbug.model.choice.ChoiceType;
import org.metacorp.mindbug.model.player.Player;

@Data
public class BooleanChoice implements IChoice<Boolean> {

    @NonNull
    private Player playerToChoose;

    @NonNull
    private CardInstance card;

    @NonNull
    private ResolvableEffect<BooleanChoiceResolver> effect;

    @Override
    public void resolve(Boolean choiceResolver, Game game) {
        // First reset choice so it doesn't block next steps
        game.setChoice(null);

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
