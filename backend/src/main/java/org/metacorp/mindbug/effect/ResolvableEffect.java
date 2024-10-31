package org.metacorp.mindbug.effect;

import org.metacorp.mindbug.choice.ChoiceList;

public interface ResolvableEffect {
    void resolve(ChoiceList choices);
}
