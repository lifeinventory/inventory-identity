package com.lifeinventory.identity.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    @Test
    @DisplayName("createAccessToken should create valid access token")
    void createAccessToken_shouldCreateValidAccessToken() {
        UUID userId = UUID.randomUUID();
        String tokenValue = "access-token-value";
        Duration validity = Duration.ofHours(1);

        Token token = Token.createAccessToken(userId, tokenValue, validity);

        assertNotNull(token.id());
        assertEquals(userId, token.userId());
        assertEquals(TokenType.ACCESS, token.type());
        assertEquals(tokenValue, token.tokenValue());
        assertFalse(token.revoked());
        assertTrue(token.isValid());
        assertFalse(token.isExpired());
    }

    @Test
    @DisplayName("createRefreshToken should create valid refresh token")
    void createRefreshToken_shouldCreateValidRefreshToken() {
        UUID userId = UUID.randomUUID();
        String tokenValue = "refresh-token-value";
        Duration validity = Duration.ofDays(30);

        Token token = Token.createRefreshToken(userId, tokenValue, validity);

        assertEquals(TokenType.REFRESH, token.type());
        assertTrue(token.isValid());
    }

    @Test
    @DisplayName("createPasswordResetToken should create valid password reset token")
    void createPasswordResetToken_shouldCreateValidPasswordResetToken() {
        UUID userId = UUID.randomUUID();
        String tokenValue = "reset-token-value";
        Duration validity = Duration.ofHours(24);

        Token token = Token.createPasswordResetToken(userId, tokenValue, validity);

        assertEquals(TokenType.PASSWORD_RESET, token.type());
        assertTrue(token.isValid());
    }

    @Test
    @DisplayName("createEmailVerificationToken should create valid verification token")
    void createEmailVerificationToken_shouldCreateValidVerificationToken() {
        UUID userId = UUID.randomUUID();
        String tokenValue = "verification-token-value";
        Duration validity = Duration.ofDays(7);

        Token token = Token.createEmailVerificationToken(userId, tokenValue, validity);

        assertEquals(TokenType.EMAIL_VERIFICATION, token.type());
        assertTrue(token.isValid());
    }

    @Test
    @DisplayName("should fail with blank token value")
    void shouldFailWithBlankTokenValue() {
        UUID userId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () ->
            Token.createAccessToken(userId, "", Duration.ofHours(1))
        );
    }

    @Test
    @DisplayName("should fail with expiresAt before createdAt")
    void shouldFailWithExpiresAtBeforeCreatedAt() {
        assertThrows(IllegalArgumentException.class, () ->
            new Token(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TokenType.ACCESS,
                "token-value",
                Instant.now().minus(Duration.ofHours(1)),
                Instant.now(),
                false
            )
        );
    }

    @Test
    @DisplayName("revoke should set revoked to true")
    void revoke_shouldSetRevokedToTrue() {
        Token token = Token.createAccessToken(
            UUID.randomUUID(),
            "token-value",
            Duration.ofHours(1)
        );

        assertFalse(token.revoked());

        Token revoked = token.revoke();

        assertTrue(revoked.revoked());
        assertFalse(revoked.isValid());
    }

    @Test
    @DisplayName("revoke should return same instance if already revoked")
    void revoke_shouldReturnSameInstanceIfAlreadyRevoked() {
        Token token = Token.createAccessToken(
            UUID.randomUUID(),
            "token-value",
            Duration.ofHours(1)
        ).revoke();

        Token result = token.revoke();

        assertSame(token, result);
    }

    @Test
    @DisplayName("isExpired should return true for expired token")
    void isExpired_shouldReturnTrueForExpiredToken() {
        Token token = new Token(
            UUID.randomUUID(),
            UUID.randomUUID(),
            TokenType.ACCESS,
            "token-value",
            Instant.now().minus(Duration.ofSeconds(1)),
            Instant.now().minus(Duration.ofHours(1)),
            false
        );

        assertTrue(token.isExpired());
        assertFalse(token.isValid());
    }

    @Test
    @DisplayName("isValid should return false for revoked token")
    void isValid_shouldReturnFalseForRevokedToken() {
        Token token = Token.createAccessToken(
            UUID.randomUUID(),
            "token-value",
            Duration.ofHours(1)
        ).revoke();

        assertFalse(token.isValid());
    }

    @Test
    @DisplayName("timeToLive should return remaining time")
    void timeToLive_shouldReturnRemainingTime() {
        Token token = Token.createAccessToken(
            UUID.randomUUID(),
            "token-value",
            Duration.ofHours(1)
        );

        Duration ttl = token.timeToLive();

        assertTrue(ttl.toMinutes() > 58);
        assertTrue(ttl.toMinutes() <= 60);
    }
}
