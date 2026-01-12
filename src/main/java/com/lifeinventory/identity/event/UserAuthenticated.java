package com.lifeinventory.identity.event;

import com.lifeinventory.identity.model.User;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a user successfully authenticates.
 */
public record UserAuthenticated(
    @NonNull UUID eventId,
    @NonNull UUID userId,
    @NonNull String email,
    String ipAddress,
    String userAgent,
    @NonNull Instant occurredAt
) implements IdentityEvent {

    public static UserAuthenticated of(User user, String ipAddress, String userAgent) {
        return new UserAuthenticated(
            UUID.randomUUID(),
            user.id(),
            user.email(),
            ipAddress,
            userAgent,
            Instant.now()
        );
    }

    public static UserAuthenticated of(User user) {
        return of(user, null, null);
    }
}
