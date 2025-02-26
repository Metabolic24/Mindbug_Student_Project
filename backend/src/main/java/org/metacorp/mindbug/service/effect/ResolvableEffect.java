package org.metacorp.mindbug.service.effect;

import org.metacorp.mindbug.model.Game;

public interface ResolvableEffect<T> {
    void resolve(Game game, T choiceResolver);
}
