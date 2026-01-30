package org.metacorp.mindbug.model.effect;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonIgnoreProperties("type")
public class CostEffect extends Effect {
    public static final String TYPE = "COST";
    /**
     * The list of effects that must be resolved before other effects
     */
    @NonNull
    private List<GenericEffect> cost;

    /**
     * The list of effects to be resolved after the cost is paid
     */
    @NonNull
    private List<GenericEffect> effects;

    /**
     * Is this effect optional
     */
    private boolean optional;

    @Override
    public boolean hasCost() {
        return true;
    }
}
