package com.lifeinventory.identity.model;

import lombok.NonNull;

/**
 * Result of a successful authentication.
 * Contains the authenticated user and tokens.
 */
public record AuthenticationResult(
    @NonNull User user,
    @NonNull Token accessToken,
    @NonNull Token refreshToken
) {
    public AuthenticationResult {
        if (!accessToken.userId().equals(user.id())) {
            throw new IllegalArgumentException("Access token userId must match user id");
        }
        if (!refreshToken.userId().equals(user.id())) {
            throw new IllegalArgumentException("Refresh token userId must match user id");
        }
    }
}
