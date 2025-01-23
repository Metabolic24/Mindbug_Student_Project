package org.metacorp.mindbug.card.effect;

import org.metacorp.mindbug.choice.ChoiceList;

public interface ResolvableEffect {
    void resolve(ChoiceList choices);
}
