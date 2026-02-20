package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.BounceEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;
import org.metacorp.mindbug.utils.AppUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BounceEffectResolver extends EffectResolver<BounceEffect> implements ResolvableEffect<List<CardInstance>> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public BounceEffectResolver(BounceEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        this.effectSource = card;

        int value = effect.getValue();
        Player cardOwner = card.getOwner();
        Player opponent = cardOwner.getOpponent(game.getPlayers()).getFirst();
        Set<CardInstance> opponentCards = new HashSet<>(opponent.getBoard());

        if (!opponentCards.isEmpty()) {
            if (opponentCards.size() <= value || value < 0) {
                bounceCards(game, opponentCards);
            } else {
                game.setChoice(new TargetChoice(cardOwner, card, this, value, opponentCards));
            }
        }
    }

    private void bounceCards(Game game, Set<CardInstance> cards) {
        for (CardInstance card : cards) {
            Player cardOwner = card.getOwner();
            cardOwner.getBoard().remove(card);
            cardOwner.getHand().add(card);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        bounceCards(game, new HashSet<>(chosenTargets));
    }
}
