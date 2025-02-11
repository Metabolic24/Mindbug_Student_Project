package org.metacorp.mindbug.card.effect;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import org.metacorp.mindbug.game.Game;
import org.metacorp.mindbug.card.CardInstance;
import org.metacorp.mindbug.card.effect.destroy.DestroyEffect;
import org.metacorp.mindbug.card.effect.disableTiming.DisableTimingEffect;
import org.metacorp.mindbug.card.effect.discard.DiscardEffect;
import org.metacorp.mindbug.card.effect.draw.DrawEffect;
import org.metacorp.mindbug.card.effect.gainLp.GainEffect;
import org.metacorp.mindbug.card.effect.inflict.InflictEffect;
import org.metacorp.mindbug.card.effect.keywordUp.KeywordUpEffect;
import org.metacorp.mindbug.card.effect.noAttack.NoAttackEffect;
import org.metacorp.mindbug.card.effect.noBlock.NoBlockEffect;
import org.metacorp.mindbug.card.effect.powerUp.PowerUpEffect;
import org.metacorp.mindbug.card.effect.revive.ReviveEffect;
import org.metacorp.mindbug.card.effect.steal.StealEffect;

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
public abstract class AbstractEffect {
    /**
     * @return the Effect type
     */
    public abstract String getType();

    /**
     * Apply the current effect
     * @param game the current game
     * @param card the card that contains this effect
     */
    public abstract void apply(Game game, CardInstance card);
}
