package org.metacorp.mindbug.exception;

import java.util.UUID;

/**
 * Exception raised when a player is not found in the database
 */
public class UnknownPlayerException extends Exception {

    /**
     * Constructor
     *
     * @param playerId the player ID that has not been found in the database
     */
    public UnknownPlayerException(UUID playerId) {
        super("No player found with id: " + playerId.toString());
    }
}
