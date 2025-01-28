package org.metacorp.mindbug.choice.frenzy;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.AttackHolder;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.choice.ChoiceType;
import org.metacorp.mindbug.choice.IChoice;

@Data
public class FrenzyAttackChoice implements IChoice<AttackHolder> {
    @NonNull
    private CardInstance attackingCard;

    @Override
    public void resolve(Game game, AttackHolder attackHolder) {
        if (attackHolder == null) {
            game.nextTurn();
        }
        else {
            attackingCard.setCanAttackTwice(false);
            game.attack(attackHolder);
        }

        // Reset the choice only in any case
        game.resetChoice();
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.FRENZY;
    }
}
