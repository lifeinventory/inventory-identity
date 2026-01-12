package com.lifeinventory.identity.event;

import com.lifeinventory.identity.model.User;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a user's access token is refreshed.
 */
public record TokenRefreshed(
    @NonNull UUID eventId,
    @NonNull UUID userId,
    @NonNull Instant occurredAt
) implements IdentityEvent {

    public static TokenRefreshed of(User user) {
        return new TokenRefreshed(
            UUID.randomUUID(),
            user.id(),
            Instant.now()
        );
    }
}
