package com.lifeinventory.identity.event;

import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a user logs out.
 */
public record UserLoggedOut(
    @NonNull UUID eventId,
    @NonNull UUID userId,
    boolean allDevices,
    @NonNull Instant occurredAt
) implements IdentityEvent {

    public static UserLoggedOut singleDevice(UUID userId) {
        return new UserLoggedOut(
            UUID.randomUUID(),
            userId,
            false,
            Instant.now()
        );
    }

    public static UserLoggedOut allDevices(UUID userId) {
        return new UserLoggedOut(
            UUID.randomUUID(),
            userId,
            true,
            Instant.now()
        );
    }
}
