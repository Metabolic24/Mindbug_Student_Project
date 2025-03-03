package org.metacorp.mindbug.model.effect;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@Getter
@Setter
public class EffectQueue extends LinkedList<EffectsToApply> {
    private boolean resolvingEffect;
}
