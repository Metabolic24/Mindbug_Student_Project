package org.metacorp.mindbug.service.effect;

import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.model.effect.CostEffect;
import org.metacorp.mindbug.model.effect.Effect;
import org.metacorp.mindbug.model.effect.EffectTiming;
import org.metacorp.mindbug.model.effect.impl.BounceEffect;
import org.metacorp.mindbug.model.effect.impl.CopyEffect;
import org.metacorp.mindbug.model.effect.impl.DestroyEffect;
import org.metacorp.mindbug.model.effect.impl.DisableTimingEffect;
import org.metacorp.mindbug.model.effect.impl.DiscardEffect;
import org.metacorp.mindbug.model.effect.impl.DrawEffect;
import org.metacorp.mindbug.model.effect.impl.EvolveEffect;
import org.metacorp.mindbug.model.effect.impl.ForceAttackEffect;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.effect.impl.GiveEffect;
import org.metacorp.mindbug.model.effect.impl.InflictEffect;
import org.metacorp.mindbug.model.effect.impl.KeywordUpEffect;
import org.metacorp.mindbug.model.effect.impl.NoAttackEffect;
import org.metacorp.mindbug.model.effect.impl.NoBlockEffect;
import org.metacorp.mindbug.model.effect.impl.PowerUpEffect;
import org.metacorp.mindbug.model.effect.impl.ProtectionEffect;
import org.metacorp.mindbug.model.effect.impl.ReviveEffect;
import org.metacorp.mindbug.model.effect.impl.StealEffect;
import org.metacorp.mindbug.service.effect.impl.BounceEffectResolver;
import org.metacorp.mindbug.service.effect.impl.CopyEffectResolver;
import org.metacorp.mindbug.service.effect.impl.CostEffectResolver;
import org.metacorp.mindbug.service.effect.impl.DestroyEffectResolver;
import org.metacorp.mindbug.service.effect.impl.DisableTimingEffectResolver;
import org.metacorp.mindbug.service.effect.impl.DiscardEffectResolver;
import org.metacorp.mindbug.service.effect.impl.DrawEffectResolver;
import org.metacorp.mindbug.service.effect.impl.EvolveEffectResolver;
import org.metacorp.mindbug.service.effect.impl.ForceAttackEffectResolver;
import org.metacorp.mindbug.service.effect.impl.GainEffectResolver;
import org.metacorp.mindbug.service.effect.impl.GiveEffectResolver;
import org.metacorp.mindbug.service.effect.impl.InflictEffectResolver;
import org.metacorp.mindbug.service.effect.impl.KeywordUpEffectResolver;
import org.metacorp.mindbug.service.effect.impl.NoAttackEffectResolver;
import org.metacorp.mindbug.service.effect.impl.NoBlockEffectResolver;
import org.metacorp.mindbug.service.effect.impl.PowerUpEffectResolver;
import org.metacorp.mindbug.service.effect.impl.ProtectionEffectResolver;
import org.metacorp.mindbug.service.effect.impl.ReviveEffectResolver;
import org.metacorp.mindbug.service.effect.impl.StealEffectResolver;

/**
 * The abstract class for effect resolvers
 *
 * @param <T> the effect type
 */
@Getter
@Setter
public abstract class EffectResolver<T extends Effect> {
    /**
     * The effect being resolved
     */
    protected T effect;

    /**
     * The source card of the effect
     */
    protected CardInstance effectSource;

    /**
     * Protected constructor
     *
     * @param effect       the effect that needs to be resolved
     * @param effectSource the card which owns the effect
     */
    protected EffectResolver(T effect, CardInstance effectSource) {
        this.effect = effect;
        this.effectSource = effectSource;
    }

    /**
     * Apply the current effect
     *
     * @param game         the current game state
     * @param timing       the triggered effect timing
     */
    public abstract void apply(Game game, EffectTiming timing) throws GameStateException, WebSocketException;

    /**
     * Build the effect resolver corresponding to the input
     *
     * @param effect the effect to be resolved
     * @param <T>    the effect type
     * @return the appropriate effect resolver initialized with the input effect
     */
    public static <T extends Effect> EffectResolver<?> getResolver(T effect, CardInstance effectSource) {
        return switch (effect.getType()) {
            case BOUNCE -> new BounceEffectResolver((BounceEffect) effect, effectSource);
            case COST -> new CostEffectResolver((CostEffect) effect, effectSource);
            case COPY -> new CopyEffectResolver((CopyEffect) effect, effectSource);
            case DESTROY -> new DestroyEffectResolver((DestroyEffect) effect, effectSource);
            case DISABLE_TIMING -> new DisableTimingEffectResolver((DisableTimingEffect) effect, effectSource);
            case DISCARD -> new DiscardEffectResolver((DiscardEffect) effect, effectSource);
            case DRAW -> new DrawEffectResolver((DrawEffect) effect, effectSource);
            case EVOLVE -> new EvolveEffectResolver((EvolveEffect) effect, effectSource);
            case FORCE_ATTACK -> new ForceAttackEffectResolver((ForceAttackEffect) effect, effectSource);
            case GAIN -> new GainEffectResolver((GainEffect) effect, effectSource);
            case GIVE -> new GiveEffectResolver((GiveEffect) effect, effectSource);
            case INFLICT -> new InflictEffectResolver((InflictEffect) effect, effectSource);
            case KEYWORD_UP -> new KeywordUpEffectResolver((KeywordUpEffect) effect, effectSource);
            case NO_ATTACK -> new NoAttackEffectResolver((NoAttackEffect) effect, effectSource);
            case NO_BLOCK -> new NoBlockEffectResolver((NoBlockEffect) effect, effectSource);
            case POWER_UP -> new PowerUpEffectResolver((PowerUpEffect) effect, effectSource);
            case PROTECTION -> new ProtectionEffectResolver((ProtectionEffect) effect, effectSource);
            case REVIVE -> new ReviveEffectResolver((ReviveEffect) effect, effectSource);
            case STEAL -> new StealEffectResolver((StealEffect) effect, effectSource);
        };
    }
}
