package org.metacorp.mindbug.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class GameStateException extends Exception {

    private Map<String, Object> additionalData;

    public GameStateException(String errorMessage) {
        super("Inconsistent game state: " + errorMessage);
    }

    public GameStateException(String errorMessage, Map<String, Object> additionalData) {
        super("Inconsistent game state: " + errorMessage);
        this.additionalData = additionalData;
    }

    public GameStateException(String errorMessage, Throwable cause, Map<String, Object> additionalData) {
        super("Inconsistent game state: " + errorMessage, cause);
        this.additionalData = additionalData;
    }
}
