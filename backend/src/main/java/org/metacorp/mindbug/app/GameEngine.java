package org.metacorp.mindbug.app;

import org.metacorp.mindbug.exception.GameStateException;

@FunctionalInterface
public interface GameEngine {
    void run() throws GameStateException;
}
