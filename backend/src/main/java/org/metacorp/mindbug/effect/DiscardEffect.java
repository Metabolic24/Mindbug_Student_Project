package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.AbstractEffect;
import org.metacorp.mindbug.choice.TargetChoice;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;

import java.util.ArrayList;
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
    public void apply(Game game, CardInstance card) {
        Player opponent = card.getOwner().getOpponent(game.getPlayers());

        if (opponent.getHand().size() <= value) {
            resolve(game, new ArrayList<>(opponent.getHand()));
        } else {
            game.setChoice(new TargetChoice(opponent, card, this, value, new HashSet<>(opponent.getHand())));
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        for (CardInstance card : chosenTargets) {
            Player cardOwner = card.getOwner();
            cardOwner.getHand().remove(card);
            cardOwner.getDiscardPile().add(card);
        }
    }
}
