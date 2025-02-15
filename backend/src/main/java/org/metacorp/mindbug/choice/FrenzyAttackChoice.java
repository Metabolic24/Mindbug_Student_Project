package org.metacorp.mindbug.choice;

import lombok.Data;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.service.AttackService;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.ChoiceType;

@Data
public class FrenzyAttackChoice implements IChoice<Boolean> {
    @NonNull
    private CardInstance attackingCard;

    @Override
    public void resolve(Boolean attackAgain, Game game) throws GameStateException {
        // First reset the choice in any case (so it does not block the next steps)
        game.setChoice(null);

        if (attackAgain != null && attackAgain) {
            attackingCard.setAbleToAttackTwice(false);
            AttackService.declareAttack(attackingCard, game);
        }
        else {
            game.setCurrentPlayer(game.getOpponent());
        }
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.FRENZY;
    }
}
