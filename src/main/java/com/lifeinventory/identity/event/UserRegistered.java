package com.lifeinventory.identity.event;

import com.lifeinventory.identity.model.AuthProvider;
import com.lifeinventory.identity.model.User;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a new user registers.
 */
public record UserRegistered(
    @NonNull UUID eventId,
    @NonNull UUID userId,
    @NonNull String email,
    @NonNull AuthProvider authProvider,
    @NonNull Instant occurredAt
) implements IdentityEvent {

    public static UserRegistered of(User user) {
        return new UserRegistered(
            UUID.randomUUID(),
            user.id(),
            user.email(),
            user.authProvider(),
            Instant.now()
        );
    }
}
