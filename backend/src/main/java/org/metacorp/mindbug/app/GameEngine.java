package org.metacorp.mindbug.app;

import org.metacorp.mindbug.exception.GameStateException;

/**
 * Interface for Mindbug game engine
 */
@FunctionalInterface
public interface GameEngine {
    /**
     * Runs a started game
     *
     * @throws GameStateException if the game reaches an inconsistant state
     */
    void run() throws GameStateException;
}
