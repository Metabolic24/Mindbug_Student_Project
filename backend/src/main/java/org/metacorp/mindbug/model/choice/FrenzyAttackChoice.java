package org.metacorp.mindbug.model.choice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.utils.ChoiceUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class FrenzyAttackChoice extends AbstractChoice<Boolean> {
    @NonNull
    private CardInstance attackingCard;

    /**
     * Constructor
     * @param attackingCard the attacking card
     */
    public FrenzyAttackChoice(@NonNull CardInstance attackingCard) {
        this.attackingCard = attackingCard;
        this.playerToChoose = attackingCard.getOwner();
    }

    @Override
    public void resolve(Boolean attackAgain, Game game) throws GameStateException, WebSocketException {
        ChoiceUtils.resolveFrenzyChoice(attackAgain, this, game);
    }

    @Override
    public ChoiceType getType() {
        return ChoiceType.FRENZY;
    }
}
