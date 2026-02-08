package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.utils.ChoiceUtils;

@Data
public class FrenzyAttackChoice implements IChoice<Boolean> {
    @NonNull
    private CardInstance attackingCard;

    @Override
    public void resolve(Boolean attackAgain, Game game) throws GameStateException, WebSocketException {
        ChoiceUtils.resolveFrenzyChoice(attackAgain, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.FRENZY;
    }
}
