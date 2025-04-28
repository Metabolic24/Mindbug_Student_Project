package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
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
        boolean highest = effect.isHighest();

        Player opponent = card.getOwner().getOpponent(game.getPlayers());

        if (highest) {
            for (CardInstance highestCard : opponent.getHighestCards()) {
                setAbleToBlock(highestCard);
            }
        } else if (max != null) {
            for (CardInstance opponentCard : opponent.getBoard()) {
                if (opponentCard.getPower() <= max) {
                    setAbleToBlock(opponentCard);
                }
            }
        } else if (opponent.getBoard().size() <= value || value < 0) {
            for (CardInstance opponentCard : opponent.getBoard()) {
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
