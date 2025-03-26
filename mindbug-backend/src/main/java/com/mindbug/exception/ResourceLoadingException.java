package com.mindbug.exception;

/**
 * Exception thrown when there is an issue loading a resource.
 */
public class ResourceLoadingException extends RuntimeException {
    public ResourceLoadingException(String message) {
        super(message);
    }
}
