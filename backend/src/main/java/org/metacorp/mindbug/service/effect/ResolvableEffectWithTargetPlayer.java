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
public interface ResolvableEffectWithTargetPlayer<T> extends ResolvableEffect<T> {

    /**
     * Selects a player as the effect target
     *
     * @param game the current game state
     * @param selectedPlayer    the selected player
     */
    void selectPlayer(Game game, Player selectedPlayer) throws GameStateException, WebSocketException;

    /**
     * @return true as this effect may need player selection
     */
    default boolean maySelectPlayer() {
        return true;
    }
}
