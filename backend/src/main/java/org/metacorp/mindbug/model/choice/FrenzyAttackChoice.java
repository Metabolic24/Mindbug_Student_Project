package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.ChoiceUtils;

@Data
@EqualsAndHashCode(callSuper = false)
public class FrenzyAttackChoice extends AbstractChoice<Boolean> {
    @NonNull
    private CardInstance attackingCard;

    public FrenzyAttackChoice(Player playerToChoose, CardInstance attackingCard) {
        this.playerToChoose = playerToChoose;
        this.attackingCard = attackingCard;
    }

    @Override
    public void resolve(Boolean attackAgain, Game game) throws WebSocketException, GameStateException {
        ChoiceUtils.resolveFrenzyChoice(attackAgain, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.FRENZY;
    }
}
