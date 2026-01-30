package org.metacorp.mindbug.model.effect;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
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

/**
 * Parent class for card effect
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CopyEffect.class, name = CopyEffect.TYPE),
        @JsonSubTypes.Type(value = GainEffect.class, name = GainEffect.TYPE),
        @JsonSubTypes.Type(value = InflictEffect.class, name = InflictEffect.TYPE),
        @JsonSubTypes.Type(value = DestroyEffect.class, name = DestroyEffect.TYPE),
        @JsonSubTypes.Type(value = BounceEffect.class, name = BounceEffect.TYPE),
        @JsonSubTypes.Type(value = DisableTimingEffect.class, name = DisableTimingEffect.TYPE),
        @JsonSubTypes.Type(value = DiscardEffect.class, name = DiscardEffect.TYPE),
        @JsonSubTypes.Type(value = DrawEffect.class, name = DrawEffect.TYPE),
        @JsonSubTypes.Type(value = EvolveEffect.class, name = EvolveEffect.TYPE),
        @JsonSubTypes.Type(value = ForceAttackEffect.class, name = ForceAttackEffect.TYPE),
        @JsonSubTypes.Type(value = GiveEffect.class, name = GiveEffect.TYPE),
        @JsonSubTypes.Type(value = KeywordUpEffect.class, name = KeywordUpEffect.TYPE),
        @JsonSubTypes.Type(value = PowerUpEffect.class, name = PowerUpEffect.TYPE),
        @JsonSubTypes.Type(value = ProtectionEffect.class, name = ProtectionEffect.TYPE),
        @JsonSubTypes.Type(value = NoAttackEffect.class, name = NoAttackEffect.TYPE),
        @JsonSubTypes.Type(value = NoBlockEffect.class, name = NoBlockEffect.TYPE),
        @JsonSubTypes.Type(value = ReviveEffect.class, name = ReviveEffect.TYPE),
        @JsonSubTypes.Type(value = StealEffect.class, name = StealEffect.TYPE)}
)
public class GenericEffect extends Effect {
    /**
     * The effect type
     */
    private EffectType type;

    /**
     * Constructor
     */
    protected GenericEffect() {
        super();
    }

    /**
     * Return the priority of the effect resolution (for passive effects only)
     * (the higher the value is, the later the effect must be resolved)
     *
     * @return the priority of the effect resolution (for passive effects only)
     */
    public int getPriority() {
        // Default behavior (override it if necessary in the corresponding effect class)
        return Integer.MAX_VALUE;
    }
}
