package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DiscardEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Effect resolver for DisableTimingEffect
 */
public class DiscardEffectResolver extends EffectResolver<DiscardEffect> implements ResolvableEffect<List<CardInstance>> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public DiscardEffectResolver(DiscardEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        Player opponent = effectSource.getOwner().getOpponent(game.getPlayers());

        int value = effect.isEachEnemy() ? opponent.getBoard().size() : effect.getValue();

        Player playerToDiscard = effect.isSelf() ? effectSource.getOwner() : opponent;
        List<CardInstance> availableCards = effect.isDrawPile() ? playerToDiscard.getDrawPile() : playerToDiscard.getHand();

        if (availableCards.size() <= value || value == -1) {
            resolve(game, new ArrayList<>(availableCards));
        } else if (effect.isDrawPile()) {
            resolve(game, new ArrayList<>(availableCards.subList(0, value)));
        } else {
            game.setChoice(new TargetChoice(playerToDiscard, effectSource, this, value, new HashSet<>(availableCards)));
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        for (CardInstance card : chosenTargets) {
            Player cardOwner = card.getOwner();

            if (effect.isDrawPile()) {
                cardOwner.getDrawPile().remove(card);
            } else {
                cardOwner.getHand().remove(card);
            }

            cardOwner.getDiscardPile().add(card);
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, chosenTargets);
    }
}
