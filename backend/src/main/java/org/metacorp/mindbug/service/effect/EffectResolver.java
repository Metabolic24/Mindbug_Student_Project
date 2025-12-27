package org.metacorp.mindbug.service.effect;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.CostEffect;
import org.metacorp.mindbug.model.effect.Effect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.GenericEffect;
import org.metacorp.mindbug.model.effect.impl.*;
import org.metacorp.mindbug.service.effect.impl.*;

/**
 * The abstract class for effect resolvers
 * @param <T> the effect type
 */
@Getter
@Setter
public abstract class EffectResolver<T extends Effect> {
    protected T effect;

    /**
     * Protected constructor
     *
     * @param effect the effect that needs to be resolved
     */
    protected EffectResolver(T effect) {
        this.effect = effect;
    }

    /**
     * Apply the current effect
     *
     * @param game         the current game state
     * @param effectSource the card that triggered the effect
     */
    public abstract void apply(Game game, CardInstance effectSource, EffectTiming timing);

    /**
     * Build the effect resolver corresponding to the input
     * @param effect the effect to be resolved
     * @return the appropriate effect resolver initialized with the input effect
     * @param <T> the effect type
     */
    public static <T extends Effect> EffectResolver<?> getResolver(T effect) {
        if (effect instanceof CostEffect) {
            return new CostEffectResolver((CostEffect) effect);
        } else {
            return switch (((GenericEffect) effect).getType()) {
                case BOUNCE -> new BounceEffectResolver((BounceEffect)  effect);
                case DESTROY -> new DestroyEffectResolver((DestroyEffect) effect);
                case DISABLE_TIMING -> new DisableTimingEffectResolver((DisableTimingEffect) effect);
                case DISCARD -> new DiscardEffectResolver((DiscardEffect) effect);
                case DRAW -> new DrawEffectResolver((DrawEffect) effect);
                case EVOLVE -> new EvolveEffectResolver((EvolveEffect) effect);
                case FORCE_ATTACK -> new ForceAttackEffectResolver((ForceAttackEffect) effect);
                case GAIN -> new GainEffectResolver((GainEffect) effect);
                case GIVE -> new GiveEffectResolver((GiveEffect) effect);
                case INFLICT -> new InflictEffectResolver((InflictEffect) effect);
                case KEYWORD_UP -> new KeywordUpEffectResolver((KeywordUpEffect) effect);
                case NO_ATTACK -> new NoAttackEffectResolver((NoAttackEffect) effect);
                case NO_BLOCK -> new NoBlockEffectResolver((NoBlockEffect) effect);
                case POWER_UP -> new PowerUpEffectResolver((PowerUpEffect) effect);
                case PROTECTION -> new ProtectionEffectResolver((ProtectionEffect) effect);
                case REVIVE -> new ReviveEffectResolver((ReviveEffect) effect);
                case STEAL -> new StealEffectResolver((StealEffect) effect);
            };
        }
    }
}
