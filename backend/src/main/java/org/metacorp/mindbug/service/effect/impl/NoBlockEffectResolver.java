package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.card.CardKeyword;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.NoBlockEffect;
import org.metacorp.mindbug.model.modifier.BlockModifier;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Effect resolver for NoBlockEffect
 */
public class NoBlockEffectResolver extends EffectResolver<NoBlockEffect> implements ResolvableEffect<List<CardInstance>> {

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
        this.effectSource = card;

        int value = effect.getValue();
        Integer max = effect.getMax();
        Integer min = effect.getMin();
        CardKeyword keyword = effect.getKeyword();
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

            if (keyword != null) {
                boardCards = boardCards.filter(cardInstance -> cardInstance.hasKeyword(keyword));
            }

            availableCards = boardCards.collect(Collectors.toSet());
        }

        if (availableCards.size() <= value || value < 0) {
            setAbleToBlock(game, availableCards);
        } else {
            game.setChoice(new TargetChoice(card.getOwner(), card, this, value, new HashSet<>(opponent.getBoard())));
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> chosenTargets) {
        setAbleToBlock(game, chosenTargets);
    }

    private void setAbleToBlock(Game game, Collection<CardInstance> cards) {
        for (CardInstance card : cards) {
            card.setAbleToBlock(false);
            if (timing == EffectTiming.ATTACK) {
                card.getModifiers().add(new BlockModifier());
            }
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }
}
