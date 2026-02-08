package org.metacorp.mindbug.exception;

/**
 * Exception raised when an error occurs while sending or receiving data through WebSocket
 */
public class WebSocketException extends Exception {

    public WebSocketException(String message, Throwable cause) {
        super(message, cause);
    }
}
