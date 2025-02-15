package org.metacorp.mindbug.effect;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.utils.CardUtils;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.AbstractEffect;
import org.metacorp.mindbug.choice.TargetChoice;
import org.metacorp.mindbug.model.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

/**
 * Effect that may destroy one or more cards
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class DestroyEffect extends AbstractEffect implements ResolvableEffect<List<CardInstance>> {
    public final static String TYPE = "DESTROY";

    private Integer value;          // The number of cards to destroy, -1 if all cards should be destroyed

    private Integer min;            // The minimum power of card(s) to be destroyed
    private Integer max;            // The maximum power of card(s) to be destroyed

    private boolean lessAllies;     // Effect is active if player has less allies than the opponent
    private boolean lowest;         // Destroy the card(s) with lowest power
    private boolean selfAllowed;    // Can this effect affect current player cards

    @Override
    public void apply(Game game, CardInstance card) {
        Player currentPlayer = card.getOwner();
        Player opponent = currentPlayer.getOpponent(game.getPlayers());

        if (lessAllies && !(currentPlayer.getBoard().size() < opponent.getBoard().size())) {
            return;
        }

        if (lowest) {
            List<CardInstance> lowestCards = selfAllowed ? CardUtils.getLowestCards(game.getPlayers()) :
                    opponent.getLowestCards();
            destroyCards(game, lowestCards);
        } else {
            List<CardInstance> availableCards = new ArrayList<>();
            for (CardInstance currentCard : opponent.getBoard()) {
                if (min != null && currentCard.getPower() < min ||
                        max != null && currentCard.getPower() > max) {
                    continue;
                }

                availableCards.add(currentCard);
            }

            if (selfAllowed) {
                for (CardInstance currentCard : card.getOwner().getBoard()) {
                    if (min != null && currentCard.getPower() < min ||
                            max != null && currentCard.getPower() > max) {
                        continue;
                    }

                    availableCards.add(currentCard);
                }
            }

            if (!availableCards.isEmpty()) {
                if (availableCards.size() <= value || value < 0) {
                    destroyCards(game, availableCards);
                } else {
                    game.setChoice(new TargetChoice(currentPlayer, card, this, value, new HashSet<>(availableCards)));
                }
            }
        }
    }

    private void destroyCards(Game game, List<CardInstance> cards) {
        Queue<EffectsToApply> effectQueue = game.getEffectQueue();
        for (CardInstance card : cards) {
            GameService.defeatCard(card, effectQueue);
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        destroyCards(game, chosenTargets);
    }
}
