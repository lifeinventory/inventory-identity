package com.lifeinventory.identity.event;

import com.lifeinventory.identity.model.User;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a user's profile is updated.
 */
public record UserProfileUpdated(
    @NonNull UUID eventId,
    @NonNull UUID userId,
    @NonNull String displayName,
    @NonNull Instant occurredAt
) implements IdentityEvent {

    public static UserProfileUpdated of(User user) {
        String displayName = user.profile().displayName();
        if (displayName == null || displayName.isBlank()) {
            displayName = user.email();
        }
        return new UserProfileUpdated(
            UUID.randomUUID(),
            user.id(),
            displayName,
            Instant.now()
        );
    }
}
