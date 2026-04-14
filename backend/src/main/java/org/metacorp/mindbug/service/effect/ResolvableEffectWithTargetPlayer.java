package org.metacorp.mindbug.service.effect;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;
import org.metacorp.mindbug.model.Game;
import org.metacorp.mindbug.model.effect.impl.GainEffect;
import org.metacorp.mindbug.model.player.Player;

import jakarta.validation.constraints.NotNull;


/**
 * Interface for an effect resolver that is able to trigger a choice0
 *
 * @param <T> the data that will be returned by the choice
 */
public interface ResolvableEffectWithTargetPlayer<T>  extends ResolvableEffect<T> {
    
    

    /**
     * A Player have been chosen as target of the effect
     *
     * @param game         the current game state
     * @param p            the player associated with the choice
     */
    void resolve(Game game,  Player p) throws GameStateException, WebSocketException;
}
