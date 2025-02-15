package org.metacorp.mindbug.choice.frenzy;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.choice.ChoiceType;
import org.metacorp.mindbug.choice.IChoice;

@Data
public class FrenzyAttackChoice implements IChoice<Boolean> {
    @NonNull
    private CardInstance attackingCard;

    @Override
    public void resolve(Game game, Boolean attackAgain) {
        // First reset the choice in any case (so it does not block the next steps)
        game.resetChoice();

        if (attackAgain != null && attackAgain) {
            attackingCard.setCanAttackTwice(false);
            game.declareAttack(attackingCard);
        }
        else {
            game.nextTurn();
        }
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.FRENZY;
    }
}
