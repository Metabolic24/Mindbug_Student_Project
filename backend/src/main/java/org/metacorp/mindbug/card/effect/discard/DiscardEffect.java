package org.metacorp.mindbug.card.effect.discard;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.AbstractEffect;
import org.metacorp.mindbug.card.effect.ResolvableEffect;
import org.metacorp.mindbug.choice.target.TargetChoice;
import org.metacorp.mindbug.player.Player;

import java.util.HashSet;
import java.util.List;

/**
 * Effect that may discard one or more cards from opponent hand
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DiscardEffect extends AbstractEffect implements ResolvableEffect<List<CardInstance>> {
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
            game.setCurrentChoice(new TargetChoice(opponent, card, this, value, new HashSet<>(opponent.getHand())));
        }
    }

    @Override
    public void resolve(List<CardInstance> chosenTargets) {
        for (CardInstance card : chosenTargets) {
            Player cardOwner = card.getOwner();
            cardOwner.getHand().remove(card);
            cardOwner.getDiscardPile().add(card);
        }
    }
}
