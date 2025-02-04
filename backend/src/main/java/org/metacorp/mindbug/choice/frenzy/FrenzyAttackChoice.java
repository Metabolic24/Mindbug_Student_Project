package org.metacorp.mindbug.choice.frenzy;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.choice.ChoiceType;
import org.metacorp.mindbug.choice.IChoice;

@Data
public class FrenzyAttackChoice implements IChoice<Boolean> {
    @NonNull
    private CardInstance attackingCard;

    @Override
    public void resolve(Game game, Boolean attackAgain) {
        if (attackAgain != null && attackAgain) {
            attackingCard.setCanAttackTwice(false);
            game.declareAttack(attackingCard);
        }
        else {
            game.nextTurn();
        }

        // Reset the choice only in any case
        game.resetChoice();
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.FRENZY;
    }
}
