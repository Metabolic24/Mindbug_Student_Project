package org.metacorp.mindbug.model.effect.impl;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metacorp.mindbug.model.effect.GenericEffect;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class EvolveEffect extends GenericEffect {
    public final static String TYPE = "EVOLVE";

    private int id; // the ID of the target evolution card
}
