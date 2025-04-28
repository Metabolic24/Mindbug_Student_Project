package org.metacorp.mindbug.model.effect;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.metacorp.mindbug.model.card.CardInstance;

import java.util.List;

@Getter
@Setter
public class EffectsToApply {
    @NonNull
    private List<GenericEffect> effects;
    private CardInstance card;
    private EffectTiming timing;

    /**
     * @param effects the effect that will later be applied
     * @param card    the card related to the effect
     * @param timing  the effect timing
     */
    public EffectsToApply(List<GenericEffect> effects, CardInstance card, EffectTiming timing) {
        this.effects = effects;
        this.card = card;
        this.timing = timing;
    }
}
