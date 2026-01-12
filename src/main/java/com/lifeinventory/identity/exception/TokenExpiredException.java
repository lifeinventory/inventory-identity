package com.lifeinventory.identity.exception;

/**
 * Exception thrown when a token has expired.
 */
public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super("Token has expired");
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
