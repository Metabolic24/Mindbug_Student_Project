package org.metacorp.mindbug.service.effect.impl;

import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.ProtectionEffect;
import org.metacorp.mindbug.model.modifier.ProtectionModifier;
import org.metacorp.mindbug.service.effect.GenericEffectResolver;

/**
 * Effect resolver for ProtectionEffect
 */
public class ProtectionEffectResolver extends GenericEffectResolver<ProtectionEffect> {

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
        if (effect.isAllies()) {
            for (CardInstance currentCard : effectSource.getOwner().getBoard()) {
                if (!(currentCard.equals(effectSource))) {
                    addProtection(currentCard, timing);
                }
            }
        }

        if (effect.isSelf()) {
            addProtection(effectSource, timing);
        }
    }

    private void addProtection(CardInstance card, EffectTiming timing) {
        card.setProtection(true);
        if (timing == EffectTiming.ATTACK) {
            card.getModifiers().add(new ProtectionModifier());
        }
    }
}
