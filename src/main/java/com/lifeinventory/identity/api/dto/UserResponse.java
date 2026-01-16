package com.lifeinventory.identity.api.dto;

import com.lifeinventory.identity.model.User;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
        UUID id,
        String email,
        String displayName,
        String firstName,
        String lastName,
        String avatarUrl,
        String locale,
        String timezone,
        Set<String> roles,
        boolean emailVerified,
        Instant createdAt,
        Instant lastLoginAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.id(),
                user.email(),
                user.profile().displayName(),
                user.profile().firstName(),
                user.profile().lastName(),
                user.profile().avatarUrl(),
                user.profile().locale(),
                user.profile().timezone(),
                user.roles().stream().map(Enum::name).collect(Collectors.toSet()),
                user.emailVerified(),
                user.createdAt(),
                user.lastLoginAt()
        );
    }
}
