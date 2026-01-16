package com.lifeinventory.identity.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    private final JWTVerifier verifier;
    private final Algorithm algorithm;

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
    }

    public Optional<DecodedJWT> validateToken(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return Optional.of(jwt);
        } catch (JWTVerificationException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<UUID> extractUserId(String token) {
        return validateToken(token)
                .map(jwt -> UUID.fromString(jwt.getSubject()));
    }

    public Optional<String> extractEmail(String token) {
        return validateToken(token)
                .map(jwt -> jwt.getClaim("email").asString());
    }

    public Optional<String> extractTokenType(String token) {
        return validateToken(token)
                .map(jwt -> jwt.getClaim("type").asString());
    }

    public boolean isAccessToken(String token) {
        return extractTokenType(token)
                .map(type -> "access".equals(type))
                .orElse(false);
    }

    public boolean isRefreshToken(String token) {
        return extractTokenType(token)
                .map(type -> "refresh".equals(type))
                .orElse(false);
    }
}
