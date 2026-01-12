package com.lifeinventory.identity.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to authenticate a user with unverified email.
 */
public class EmailNotVerifiedException extends RuntimeException {

    private final UUID userId;

    public EmailNotVerifiedException(UUID userId) {
        super("Email not verified for user: " + userId);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
