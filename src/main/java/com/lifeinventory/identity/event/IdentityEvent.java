package com.lifeinventory.identity.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all identity domain events.
 * Identity events represent significant authentication and authorization events.
 */
public sealed interface IdentityEvent
    permits UserRegistered, UserAuthenticated, UserLoggedOut,
            PasswordResetRequested, PasswordChanged,
            EmailVerificationRequested, EmailVerified,
            UserProfileUpdated, TokenRefreshed {

    /**
     * Unique identifier for this event.
     */
    UUID eventId();

    /**
     * ID of the user this event relates to.
     */
    UUID userId();

    /**
     * When this event occurred.
     */
    Instant occurredAt();

    /**
     * Type of event for serialization/deserialization.
     */
    default String eventType() {
        return this.getClass().getSimpleName();
    }
}
