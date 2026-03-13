package org.metacorp.mindbug.app;

import org.metacorp.mindbug.exception.GameStateException;
import org.metacorp.mindbug.exception.WebSocketException;

/**
 * Backward-compatible entry point for auto 2v2 mode.
 * Prefer using AutoApp.main with args (\"2v2\").
 */
public class AutoApp2v2 {
    public static void main() throws GameStateException, WebSocketException {
        AutoApp.main(new String[] {"2v2"});
    }
}
