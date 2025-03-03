package org.metacorp.mindbug.service.effect;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.GenericEffect;
import org.metacorp.mindbug.model.effect.impl.*;
import org.metacorp.mindbug.service.effect.impl.*;

@Getter
@Setter
public abstract class GenericEffectResolver<T extends GenericEffect> {
    protected T effect;

    /**
     * Protected constructor
     * @param effect the effect that needs to be resolved
     */
    protected GenericEffectResolver(T effect) {
        this.effect = effect;
    }

    /**
     * Apply the current effect
     * @param game the current game state
     * @param effectSource the card that triggered the effect
     */
    public abstract void apply(Game game, CardInstance effectSource);

    public static <T extends GenericEffect> GenericEffectResolver<?> getResolver(T effect) {
        return switch (effect.getType()) {
            case DESTROY -> new DestroyEffectResolver((DestroyEffect) effect);
            case DISABLE_TIMING -> new DisableTimingEffectResolver((DisableTimingEffect) effect);
            case DISCARD -> new DiscardEffectResolver((DiscardEffect) effect);
            case DRAW -> new DrawEffectResolver((DrawEffect) effect);
            case GAIN -> new GainEffectResolver((GainEffect) effect);
            case INFLICT -> new InflictEffectResolver((InflictEffect) effect);
            case KEYWORD_UP -> new KeywordUpEffectResolver((KeywordUpEffect) effect);
            case NO_ATTACK -> new NoAttackEffectResolver((NoAttackEffect) effect);
            case NO_BLOCK -> new NoBlockEffectResolver((NoBlockEffect) effect);
            case POWER_UP -> new PowerUpEffectResolver((PowerUpEffect) effect);
            case REVIVE -> new ReviveEffectResolver((ReviveEffect) effect);
            case STEAL -> new StealEffectResolver((StealEffect) effect);
        };
    }
}
