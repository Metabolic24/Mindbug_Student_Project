package org.metacorp.mindbug.service.effect;

import org.metacorp.mindbug.model.Game;

/**
 * Interface for an effect resolver that is able to trigger a choice0
 *
 * @param <T> the data that will be returned by the choice
 */
public interface ResolvableEffect<T> {
    /**
     * Resolve the choice source effect using the choice result
     *
     * @param game         the current game state
     * @param choiceResult the choice result
     */
    void resolve(Game game, T choiceResult);
}
