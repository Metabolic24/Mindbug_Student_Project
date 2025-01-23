package org.metacorp.mindbug.card.effect.revive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.card.effect.ResolvableEffect;
import org.metacorp.mindbug.player.Player;
import org.metacorp.mindbug.choice.Choice;
import org.metacorp.mindbug.choice.ChoiceList;
import org.metacorp.mindbug.choice.ChoiceLocation;

import java.util.List;

/**
 * Effect that revives the current card on some specific conditions
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ReviveEffect extends AbstractEffect implements ResolvableEffect {
    public final static String TYPE = "REVIVE";

    private boolean loseLife; // Should card revive when losing life points

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        if (loseLife) {
            game.setChoiceList(new ChoiceList(card.getOwner(), 0, List.of(new Choice(card, ChoiceLocation.DISCARD)), this, card));
        }
    }

    @Override
    public void resolve(ChoiceList choiceList) {
        if (choiceList != null && !choiceList.getChoices().isEmpty()) {
            if (choiceList.getChoices().size() == 1) {
                // We consider that there can be only one choice available
                CardInstance revivedCard = choiceList.getChoices().getFirst().getCard();
                Player currentPlayer = choiceList.getPlayerToChoose();

                revivedCard.setOwner(currentPlayer);
                currentPlayer.getDiscardPile().remove(revivedCard);
                currentPlayer.getBoard().add(revivedCard);
            } else {
                // TODO Raise an error
            }
        }
    }
}
