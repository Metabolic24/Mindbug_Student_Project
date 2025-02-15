package org.metacorp.mindbug.model.effect;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.model.card.CardInstance;
import org.metacorp.mindbug.effect.DestroyEffect;
import org.metacorp.mindbug.effect.DisableTimingEffect;
import org.metacorp.mindbug.effect.DiscardEffect;
import org.metacorp.mindbug.effect.DrawEffect;
import org.metacorp.mindbug.effect.GainEffect;
import org.metacorp.mindbug.effect.InflictEffect;
import org.metacorp.mindbug.effect.KeywordUpEffect;
import org.metacorp.mindbug.effect.NoAttackEffect;
import org.metacorp.mindbug.effect.NoBlockEffect;
import org.metacorp.mindbug.effect.PowerUpEffect;
import org.metacorp.mindbug.effect.ReviveEffect;
import org.metacorp.mindbug.effect.steal.StealEffect;
import org.metacorp.mindbug.model.Game;

/** Abstract class for card effect */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GainEffect.class, name = GainEffect.TYPE),
        @JsonSubTypes.Type(value = InflictEffect.class, name = InflictEffect.TYPE),
        @JsonSubTypes.Type(value = DestroyEffect.class, name = DestroyEffect.TYPE),
        @JsonSubTypes.Type(value = DisableTimingEffect.class, name = DisableTimingEffect.TYPE),
        @JsonSubTypes.Type(value = DiscardEffect.class, name = DiscardEffect.TYPE),
        @JsonSubTypes.Type(value = DrawEffect.class, name = DrawEffect.TYPE),
        @JsonSubTypes.Type(value = KeywordUpEffect.class, name = KeywordUpEffect.TYPE),
        @JsonSubTypes.Type(value = PowerUpEffect.class, name = PowerUpEffect.TYPE),
        @JsonSubTypes.Type(value = NoAttackEffect.class, name = NoAttackEffect.TYPE),
        @JsonSubTypes.Type(value = NoBlockEffect.class, name = NoBlockEffect.TYPE),
        @JsonSubTypes.Type(value = ReviveEffect.class, name = ReviveEffect.TYPE),
        @JsonSubTypes.Type(value = StealEffect.class, name = StealEffect.TYPE)}
)
public abstract class AbstractEffect {

    private EffectType type;

    /**
     * Apply the current effect
     * @param game the current game
     * @param card the card that contains this effect
     */
    public abstract void apply(Game game, CardInstance card);
}
