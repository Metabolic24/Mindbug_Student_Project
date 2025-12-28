package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DestroyEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.service.game.CardService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.metacorp.mindbug.service.game.CardService.defeatCard;

/**
 * Effect resolver for DestroyEffect
 */
public class DestroyEffectResolver extends EffectResolver<DestroyEffect> implements ResolvableEffect<List<CardInstance>> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public DestroyEffectResolver(DestroyEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        Integer value = effect.getValue();
        Integer min = effect.getMin();
        Integer max = effect.getMax();
        boolean selfAllowed = effect.isSelfAllowed();
        boolean allies = effect.isAllies();

        Player currentPlayer = card.getOwner();
        Player opponent = currentPlayer.getOpponent(game.getPlayers());

        if (effect.isLessAllies() && !(currentPlayer.getBoard().size() < opponent.getBoard().size())) {
            return;
        }

        if (effect.isItself()) {
            destroyCards(game, Collections.singletonList(card));
        } else if (effect.isLowest()) {
            List<CardInstance> lowestCards = selfAllowed ? CardService.getLowestCards(game.getPlayers()) :
                    opponent.getLowestCards();
            destroyCards(game, lowestCards);
        } else {
            List<CardInstance> availableCards = new ArrayList<>();

            if (!allies) {
                for (CardInstance currentCard : opponent.getBoard()) {
                    if (min != null && currentCard.getPower() < min ||
                            max != null && currentCard.getPower() > max) {
                        continue;
                    }

                    availableCards.add(currentCard);
                }
            }

            if (allies || selfAllowed) {
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
        for (CardInstance card : cards) {
            defeatCard(card, game);
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        destroyCards(game, chosenTargets);
    }
}
