package org.metacorp.mindbug.card.effect;

import org.metacorp.mindbug.game.Game;

public interface ResolvableEffect<T> {
    void resolve(Game game, T choiceResolver);
}
