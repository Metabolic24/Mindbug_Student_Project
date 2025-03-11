package org.metacorp.mindbug.exception;

import lombok.Getter;

import java.util.Map;

/**
 * Exception raised when a game reaches an unexpected state
 */
@Getter
public class GameStateException extends Exception {

    private Map<String, Object> additionalData;

    /**
     * Constructor
     * @param errorMessage the error message
     */
    public GameStateException(String errorMessage) {
        super("Inconsistent game state: " + errorMessage);
    }

    /**
     * Constructor
     * @param errorMessage the error message
     * @param additionalData additional data to be included in the exception structure
     */
    public GameStateException(String errorMessage, Map<String, Object> additionalData) {
        super("Inconsistent game state: " + errorMessage);
        this.additionalData = additionalData;
    }

    /**
     * Constructor
     * @param errorMessage the error
     * @param cause the exception cause
     * @param additionalData additional data to be included in the exception structure
     */
    public GameStateException(String errorMessage, Throwable cause, Map<String, Object> additionalData) {
        super("Inconsistent game state: " + errorMessage, cause);
        this.additionalData = additionalData;
    }
}
