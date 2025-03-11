package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.effect.impl.DestroyEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.GameService;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.utils.CardUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Effect resolver for DestroyEffect
 */
public class DestroyEffectResolver extends GenericEffectResolver<DestroyEffect> implements ResolvableEffect<List<CardInstance>> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public DestroyEffectResolver(DestroyEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card) {
        Integer value = effect.getValue();
        Integer min = effect.getMin();
        Integer max = effect.getMax();
        boolean lessAllies = effect.isLessAllies();
        boolean lowest = effect.isLowest();
        boolean selfAllowed = effect.isSelfAllowed();

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
        EffectQueue effectQueue = game.getEffectQueue();
        for (CardInstance card : cards) {
            GameService.defeatCard(card, effectQueue);
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        destroyCards(game, chosenTargets);
    }
}
