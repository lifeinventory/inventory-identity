package com.lifeinventory.identity.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lifeinventory.identity.model.Token;
import com.lifeinventory.identity.model.User;
import com.lifeinventory.identity.service.TokenGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenGenerator implements TokenGenerator {

    private final Algorithm algorithm;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;
    private static final Duration PASSWORD_RESET_EXPIRATION = Duration.ofHours(1);
    private static final Duration EMAIL_VERIFICATION_EXPIRATION = Duration.ofHours(24);

    public JwtTokenGenerator(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-expiration}") long accessTokenExpirationMs,
            @Value("${security.jwt.refresh-token-expiration}") long refreshTokenExpirationMs
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.accessTokenExpiration = Duration.ofMillis(accessTokenExpirationMs);
        this.refreshTokenExpiration = Duration.ofMillis(refreshTokenExpirationMs);
    }

    @Override
    public Token generateAccessToken(User user) {
        String tokenValue = createJwt(user, accessTokenExpiration, "access");
        return Token.createAccessToken(user.id(), tokenValue, accessTokenExpiration);
    }

    @Override
    public Token generateRefreshToken(User user) {
        String tokenValue = createJwt(user, refreshTokenExpiration, "refresh");
        return Token.createRefreshToken(user.id(), tokenValue, refreshTokenExpiration);
    }

    @Override
    public Token generatePasswordResetToken(User user) {
        String tokenValue = createJwt(user, PASSWORD_RESET_EXPIRATION, "password_reset");
        return Token.createPasswordResetToken(user.id(), tokenValue, PASSWORD_RESET_EXPIRATION);
    }

    @Override
    public Token generateEmailVerificationToken(User user) {
        String tokenValue = createJwt(user, EMAIL_VERIFICATION_EXPIRATION, "email_verification");
        return Token.createEmailVerificationToken(user.id(), tokenValue, EMAIL_VERIFICATION_EXPIRATION);
    }

    private String createJwt(User user, Duration expiration, String tokenType) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(user.id().toString())
                .withClaim("email", user.email())
                .withClaim("type", tokenType)
                .withClaim("roles", user.roles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()))
                .withIssuedAt(now)
                .withExpiresAt(now.plus(expiration))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }
}
