package org.metacorp.mindbug.service.effect;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.player.Player;

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
    void resolve(Game game, T choiceResult) throws GameStateException, WebSocketException;

    /**
     * A Player have been chosen as target of the effect
     *
     * @param game the current game state
     * @param p    the player associated with the choice
     */
    void resolve(Game game, Player p) throws GameStateException, WebSocketException;
}
