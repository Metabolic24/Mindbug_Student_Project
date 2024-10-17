package org.metacorp.mindbug;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.effect.*;

/** Abstract class for card effect */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY)
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
public abstract class Effect {
    /**
     * @return the Effect type
     */
    public abstract String getType();
}
