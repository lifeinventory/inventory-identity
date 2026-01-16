package com.lifeinventory.identity.infrastructure.persistence.cassandra.mapper;

import com.lifeinventory.identity.infrastructure.persistence.cassandra.entity.TokenEntity;
import com.lifeinventory.identity.infrastructure.persistence.cassandra.entity.UserEntity;
import com.lifeinventory.identity.model.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityMapper {

    public UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.id())
                .email(user.email())
                .passwordHash(user.passwordHash())
                .authProvider(user.authProvider().name())
                .externalId(user.externalId())
                .displayName(user.profile().displayName())
                .firstName(user.profile().firstName())
                .lastName(user.profile().lastName())
                .avatarUrl(user.profile().avatarUrl())
                .locale(user.profile().locale())
                .timezone(user.profile().timezone())
                .roles(user.roles().stream().map(Role::name).collect(Collectors.toSet()))
                .permissions(user.permissions().stream().map(Permission::name).collect(Collectors.toSet()))
                .emailVerified(user.emailVerified())
                .active(user.active())
                .lastLoginAt(user.lastLoginAt())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt())
                .build();
    }

    public User toDomain(UserEntity entity) {
        UserProfile profile = new UserProfile(
                entity.getDisplayName(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getAvatarUrl(),
                entity.getLocale(),
                entity.getTimezone()
        );

        Set<Role> roles = entity.getRoles() != null
                ? entity.getRoles().stream().map(Role::valueOf).collect(Collectors.toSet())
                : Set.of();

        Set<Permission> permissions = entity.getPermissions() != null
                ? entity.getPermissions().stream().map(Permission::valueOf).collect(Collectors.toSet())
                : Set.of();

        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                AuthProvider.valueOf(entity.getAuthProvider()),
                entity.getExternalId(),
                profile,
                roles,
                permissions,
                entity.isEmailVerified(),
                entity.isActive(),
                entity.getLastLoginAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public TokenEntity toEntity(Token token) {
        return TokenEntity.builder()
                .id(token.id())
                .userId(token.userId())
                .tokenType(token.type().name())
                .tokenValue(token.tokenValue())
                .expiresAt(token.expiresAt())
                .createdAt(token.createdAt())
                .revoked(token.revoked())
                .build();
    }

    public Token toDomain(TokenEntity entity) {
        return new Token(
                entity.getId(),
                entity.getUserId(),
                TokenType.valueOf(entity.getTokenType()),
                entity.getTokenValue(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.isRevoked()
        );
    }
}
