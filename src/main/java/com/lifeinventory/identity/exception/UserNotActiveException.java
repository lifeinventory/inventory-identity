package com.lifeinventory.identity.exception;

import java.util.UUID;

/**
 * Exception thrown when attempting to authenticate a deactivated user.
 */
public class UserNotActiveException extends RuntimeException {

    private final UUID userId;

    public UserNotActiveException(UUID userId) {
        super("User account is not active: " + userId);
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
