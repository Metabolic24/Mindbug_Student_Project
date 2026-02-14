package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.choice.TargetChoice;
import org.metacorp.mindbug.model.effect.Effect;
import org.metacorp.mindbug.model.effect.EffectQueue;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.EffectsToApply;
import org.metacorp.mindbug.model.effect.impl.CopyEffect;
import org.metacorp.mindbug.model.player.Player;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;
import org.metacorp.mindbug.service.effect.ResolvableEffect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Effect resolver for CopyEffect
 */
public class CopyEffectResolver extends EffectResolver<CopyEffect> implements ResolvableEffect<List<CardInstance>> {

    /**
     * Constructor
     *
     * @param effect       the effect to be resolved
     * @param effectSource the card which owns the effect
     */
    public CopyEffectResolver(CopyEffect effect, CardInstance effectSource) {
        super(effect, effectSource);
    }

    @Override
    public void apply(Game game, EffectTiming timing) {
        Player sourceOwner = effectSource.getOwner();
        Player opponent = sourceOwner.getOpponent(game.getPlayers());

        List<CardInstance> availableCards = new ArrayList<>();
        for (CardInstance card : opponent.getBoard()) {
            if (!card.getEffects(effect.getTiming()).isEmpty()) {
                availableCards.add(card);
            }
        }

        for (CardInstance card : sourceOwner.getBoard()) {
            if (!card.getEffects(effect.getTiming()).isEmpty()) {
                availableCards.add(card);
            }
        }

        if (!availableCards.isEmpty()) {
            if (availableCards.size() == 1) {
                resolve(game, availableCards);
            } else {
                game.setChoice(new TargetChoice(sourceOwner, effectSource, this, 1, new HashSet<>(availableCards)));
            }
        }
    }

    @Override
    public void resolve(Game game, List<CardInstance> choiceResult) {
        List<Effect> effects = choiceResult.getFirst().getEffects(effect.getTiming());

        EffectQueue effectQueue = game.getEffectQueue();
        effectQueue.push(new EffectsToApply(effects, effectSource, effect.getTiming()));
        effectQueue.setResolvingEffect(true);

        HistoryService.logEffect(game, effect.getType(), effectSource, choiceResult);
    }
}
