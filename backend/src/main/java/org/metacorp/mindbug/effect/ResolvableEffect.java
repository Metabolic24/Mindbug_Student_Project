package org.metacorp.mindbug.effect;

import org.metacorp.mindbug.model.Game;

public interface ResolvableEffect<T> {
    void resolve(Game game, T choiceResolver);
}
