package org.metacorp.mindbug.card.effect.discard;

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
import java.util.stream.Collectors;

/** Effect that may discard one or more cards from opponent hand */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DiscardEffect extends AbstractEffect implements ResolvableEffect {
    public final static String TYPE = "DISCARD";

    private int value; // The number of cards to be discarded

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Player opponent = card.getOwner().getOpponent(game.getPlayers());

        if (opponent.getHand().size() <= value) {
            opponent.getDiscardPile().addAll(opponent.getHand());
            opponent.getHand().clear();
        } else {
            List<Choice> choices = opponent.getHand().stream()
                    .map(opponentCard -> new Choice(opponentCard, ChoiceLocation.HAND))
                    .collect(Collectors.toList());

            game.setChoiceList(new ChoiceList(opponent, value, choices, this, card));
        }
    }

    @Override
    public void resolve(ChoiceList choiceList) {
        if (choiceList != null && !choiceList.getChoices().isEmpty()) {
            List<Choice> choices = choiceList.getChoices();
            if (choices.size() == value) {
                for (Choice choice : choices) {
                    CardInstance cardToDiscard = choice.getCard();
                    cardToDiscard.getOwner().getHand().remove(cardToDiscard);
                    cardToDiscard.getOwner().getDiscardPile().add(cardToDiscard);
                }
            } else {
                //TODO Raise an error
            }
        }
    }
}
