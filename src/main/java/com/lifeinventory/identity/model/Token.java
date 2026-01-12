package com.lifeinventory.identity.model;

import lombok.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents an authentication token (access, refresh, password reset, etc.).
 * Immutable value object.
 */
public record Token(
    @NonNull UUID id,
    @NonNull UUID userId,
    @NonNull TokenType type,
    @NonNull String tokenValue,
    @NonNull Instant expiresAt,
    @NonNull Instant createdAt,
    boolean revoked
) {
    public Token {
        if (tokenValue.isBlank()) {
            throw new IllegalArgumentException("tokenValue must not be blank");
        }
        if (expiresAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("expiresAt must be after createdAt");
        }
    }

    /**
     * Create a new access token.
     */
    public static Token createAccessToken(UUID userId, String tokenValue, Duration validity) {
        var now = Instant.now();
        return new Token(
            UUID.randomUUID(),
            userId,
            TokenType.ACCESS,
            tokenValue,
            now.plus(validity),
            now,
            false
        );
    }

    /**
     * Create a new refresh token.
     */
    public static Token createRefreshToken(UUID userId, String tokenValue, Duration validity) {
        var now = Instant.now();
        return new Token(
            UUID.randomUUID(),
            userId,
            TokenType.REFRESH,
            tokenValue,
            now.plus(validity),
            now,
            false
        );
    }

    /**
     * Create a password reset token.
     */
    public static Token createPasswordResetToken(UUID userId, String tokenValue, Duration validity) {
        var now = Instant.now();
        return new Token(
            UUID.randomUUID(),
            userId,
            TokenType.PASSWORD_RESET,
            tokenValue,
            now.plus(validity),
            now,
            false
        );
    }

    /**
     * Create an email verification token.
     */
    public static Token createEmailVerificationToken(UUID userId, String tokenValue, Duration validity) {
        var now = Instant.now();
        return new Token(
            UUID.randomUUID(),
            userId,
            TokenType.EMAIL_VERIFICATION,
            tokenValue,
            now.plus(validity),
            now,
            false
        );
    }

    public Token revoke() {
        if (revoked) {
            return this;
        }
        return new Token(id, userId, type, tokenValue, expiresAt, createdAt, true);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    public Duration timeToLive() {
        return Duration.between(Instant.now(), expiresAt);
    }
}
