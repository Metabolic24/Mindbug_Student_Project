package org.metacorp.mindbug.card.effect;

import org.metacorp.mindbug.Game;

public interface ResolvableEffect<T> {
    void resolve(T choiceResolver);
}
