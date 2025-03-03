package org.metacorp.mindbug.model.effect;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.metacorp.mindbug.model.card.CardInstance;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EffectsToApply {
    @NonNull
    private List<GenericEffect> effects;
    private CardInstance card;

    /**
     * @param effects the effect that will later be applied
     * @param card the card related to the effect
     */
    public EffectsToApply(List<GenericEffect> effects, CardInstance card) {
        this.effects = effects;
        this.card = card;
    }
}
