package org.metacorp.mindbug.exception;

/**
 * Exception dedicated to card sets issues
 */
public class CardSetException extends Exception {

    /**
     * Constructor
     *
     * @param message the error message
     */
    public CardSetException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param cause the error cause
     */
    public CardSetException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param message the error message
     * @param cause   the error cause
     */
    public CardSetException(String message, Throwable cause) {
        super(message, cause);
    }
}
