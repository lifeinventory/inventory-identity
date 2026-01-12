package com.lifeinventory.identity.model;

import lombok.NonNull;

/**
 * Value object representing user credentials for authentication.
 */
public record Credentials(
    @NonNull String email,
    @NonNull String password
) {
    public Credentials {
        if (email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }
    }

    /**
     * Normalize email for comparison.
     */
    public String normalizedEmail() {
        return email.toLowerCase().trim();
    }
}
