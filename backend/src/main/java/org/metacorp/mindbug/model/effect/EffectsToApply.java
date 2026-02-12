package org.metacorp.mindbug.model.effect;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.metacorp.mindbug.model.card.CardInstance;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EffectsToApply {
    /**
     * The list of effects to be resolved after the cost is paid
     */
    @NonNull
    private List<Effect> effects;

    /**
     * The source card of the effects
     */
    @NonNull
    private CardInstance card;

    /**
     * The effects timing
     */
    @NonNull
    private EffectTiming timing;

    /**
     * The list of effects that must be resolved before other effects
     */
    private List<GenericEffect> cost;

    /**
     * Constructor
     *
     * @param effects the effect that will later be applied
     * @param card    the card related to the effect
     * @param timing  the effect timing
     */
    public EffectsToApply(@NonNull List<Effect> effects, @NonNull CardInstance card, @NonNull EffectTiming timing) {
        this.effects = effects;
        this.card = card;
        this.timing = timing;
    }

    /**
     * Constructor that should only be used for CostEffect resolution
     *
     * @param cost    the cost effects that are required to apply effects
     * @param effects the effect that will later be applied
     * @param card    the card related to the effect
     * @param timing  the effect timing
     */
    public EffectsToApply(@NonNull List<GenericEffect> cost, @NonNull List<GenericEffect> effects, @NonNull CardInstance card,
                          @NonNull EffectTiming timing) {
        this(new ArrayList<>(effects), card, timing);
        this.cost = cost;
    }

    /**
     * @return true if the instance has cost effect(s)
     */
    public boolean hasCost() {
        return this.cost != null && !this.cost.isEmpty();
    }
}
