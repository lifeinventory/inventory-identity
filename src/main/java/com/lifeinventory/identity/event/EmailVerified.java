package com.lifeinventory.identity.event;

import com.lifeinventory.identity.model.User;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a user's email is verified.
 */
public record EmailVerified(
    @NonNull UUID eventId,
    @NonNull UUID userId,
    @NonNull String email,
    @NonNull Instant occurredAt
) implements IdentityEvent {

    public static EmailVerified of(User user) {
        return new EmailVerified(
            UUID.randomUUID(),
            user.id(),
            user.email(),
            Instant.now()
        );
    }
}
