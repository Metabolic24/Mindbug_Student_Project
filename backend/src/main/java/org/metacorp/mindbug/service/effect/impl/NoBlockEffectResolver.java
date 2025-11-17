package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.Card;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.NoBlockEffect;
import org.metacorp.mindbug.model.modifier.BlockModifier;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Effect resolver for NoBlockEffect
 */
public class NoBlockEffectResolver extends GenericEffectResolver<NoBlockEffect> implements ResolvableEffect<List<CardInstance>> {

    private EffectTiming timing;

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public NoBlockEffectResolver(NoBlockEffect effect) {
        super(effect);
    }

    @Override
    public void apply(Game game, CardInstance card, EffectTiming timing) {
        this.timing = timing;

        int value = effect.getValue();
        Integer max = effect.getMax();
        Integer min = effect.getMin();
        boolean highest = effect.isHighest();

        Player opponent = card.getOwner().getOpponent(game.getPlayers());
        Set<CardInstance> availableCards;

        if (highest) {
            availableCards = new HashSet<>(opponent.getHighestCards());
        } else {
            Stream<CardInstance> boardCards = opponent.getBoard().stream();

            if (max != null) {
                boardCards = boardCards.filter(cardInstance -> cardInstance.getPower() <= max);
            }

            if (min != null) {
                boardCards = boardCards.filter(cardInstance -> cardInstance.getPower() >= min);
            }

            availableCards = boardCards.collect(Collectors.toSet());
        }

        if (availableCards.size() <= value || value < 0) {
            for (CardInstance opponentCard : availableCards) {
                setAbleToBlock(opponentCard);
            }
        } else {
            game.setChoice(new TargetChoice(card.getOwner(), card, this, value, new HashSet<>(opponent.getBoard())));
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        for (CardInstance card : chosenTargets) {
            setAbleToBlock(card);
        }
    }

    private void setAbleToBlock(CardInstance card) {
        card.setAbleToBlock(false);
        if (timing == EffectTiming.ATTACK) {
            card.getModifiers().add(new BlockModifier());
        }
    }
}
