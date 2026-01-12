package com.lifeinventory.identity.event;

import com.lifeinventory.identity.model.Token;
import com.lifeinventory.identity.model.User;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a user requests a password reset.
 */
public record PasswordResetRequested(
    @NonNull UUID eventId,
    @NonNull UUID userId,
    @NonNull String email,
    @NonNull UUID tokenId,
    @NonNull Instant expiresAt,
    @NonNull Instant occurredAt
) implements IdentityEvent {

    public static PasswordResetRequested of(User user, Token token) {
        return new PasswordResetRequested(
            UUID.randomUUID(),
            user.id(),
            user.email(),
            token.id(),
            token.expiresAt(),
            Instant.now()
        );
    }
}
