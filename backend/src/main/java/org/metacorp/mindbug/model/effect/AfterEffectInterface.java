package org.metacorp.mindbug.model.effect;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;

@FunctionalInterface
public interface AfterEffectInterface {

    void run() throws GameStateException, WebSocketException;
}
