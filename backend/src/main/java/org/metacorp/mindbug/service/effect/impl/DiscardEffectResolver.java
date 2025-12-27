package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.DiscardEffect;
import org.metacorp.mindbug.model.player.Player;
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
     * @param effect the effect to be resolved
     */
    public DiscardEffectResolver(DiscardEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        Player opponent = card.getOwner().getOpponent(game.getPlayers());

        int value;
        if (effect.isEachEnemy()) {
            value = opponent.getBoard().size();
        } else {
            value = effect.getValue();
        }

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
