package com.lifeinventory.identity.exception;

/**
 * Exception thrown when a token is invalid.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid token");
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}
