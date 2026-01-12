package com.lifeinventory.identity.event;

import com.lifeinventory.identity.model.User;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a user's password is changed.
 */
public record PasswordChanged(
    @NonNull UUID eventId,
    @NonNull UUID userId,
    @NonNull String email,
    @NonNull Instant occurredAt
) implements IdentityEvent {

    public static PasswordChanged of(User user) {
        return new PasswordChanged(
            UUID.randomUUID(),
            user.id(),
            user.email(),
            Instant.now()
        );
    }
}
