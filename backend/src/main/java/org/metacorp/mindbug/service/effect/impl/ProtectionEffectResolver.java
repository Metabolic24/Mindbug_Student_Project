package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.ProtectionEffect;
import org.metacorp.mindbug.model.modifier.ProtectionModifier;
import org.metacorp.mindbug.service.HistoryService;
import org.metacorp.mindbug.service.effect.EffectResolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Effect resolver for ProtectionEffect
 */
public class ProtectionEffectResolver extends EffectResolver<ProtectionEffect> {

    /**
     * Constructor
     *
     * @param effect the effect to be resolved
     */
    public ProtectionEffectResolver(ProtectionEffect effect) {
        super(effect);
    }


    @Override
    public void apply(Game game, CardInstance effectSource, EffectTiming timing) {
        this.effectSource = effectSource;

        Set<CardInstance> availableCards = new HashSet<>();

        if (effect.isSelf()) {
            availableCards.add(effectSource);
        }

        if (effect.isAllies()) {
            effectSource.getOwner().getBoard().stream().filter(currentCard -> !currentCard.equals(effectSource)).forEach(availableCards::add);
        }

        addProtection(game, availableCards, timing);
    }

    private void addProtection(Game game, Collection<CardInstance> cards, EffectTiming timing) {
        for (CardInstance card : cards) {
            card.setProtection(true);
            if (timing == EffectTiming.ATTACK) {
                card.getModifiers().add(new ProtectionModifier());
            }
        }

        HistoryService.logEffect(game, effect.getType(), effectSource, cards);
    }
}
