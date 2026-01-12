package com.lifeinventory.identity.model;

import lombok.NonNull;

import java.time.Instant;
import java.util.*;

/**
 * Core domain entity representing a user.
 * Immutable aggregate root.
 */
public record User(
    @NonNull UUID id,
    @NonNull String email,
    String passwordHash,
    @NonNull AuthProvider authProvider,
    String externalId,
    @NonNull UserProfile profile,
    @NonNull Set<Role> roles,
    @NonNull Set<Permission> permissions,
    boolean emailVerified,
    boolean active,
    Instant lastLoginAt,
    @NonNull Instant createdAt,
    @NonNull Instant updatedAt
) {
    public User {
        if (email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (authProvider == AuthProvider.LOCAL && (passwordHash == null || passwordHash.isBlank())) {
            throw new IllegalArgumentException("passwordHash is required for LOCAL auth provider");
        }
        if (authProvider != AuthProvider.LOCAL && (externalId == null || externalId.isBlank())) {
            throw new IllegalArgumentException("externalId is required for external auth providers");
        }
        // Make defensive copies
        roles = Set.copyOf(roles);
        permissions = Set.copyOf(permissions);
    }

    /**
     * Create a new local user with email/password.
     */
    public static User createLocal(String email, String passwordHash) {
        var now = Instant.now();
        return new User(
            UUID.randomUUID(),
            email.toLowerCase().trim(),
            passwordHash,
            AuthProvider.LOCAL,
            null,
            UserProfile.empty(),
            Set.of(Role.USER),
            defaultPermissionsForRole(Role.USER),
            false,
            true,
            null,
            now,
            now
        );
    }

    /**
     * Create a new user from external OAuth provider.
     */
    public static User createExternal(String email, AuthProvider provider, String externalId, UserProfile profile) {
        var now = Instant.now();
        return new User(
            UUID.randomUUID(),
            email.toLowerCase().trim(),
            null,
            provider,
            externalId,
            profile,
            Set.of(Role.USER),
            defaultPermissionsForRole(Role.USER),
            true, // External providers verify email
            true,
            now,
            now,
            now
        );
    }

    // ===== State transition methods =====

    public User markEmailVerified() {
        if (emailVerified) {
            return this;
        }
        return new User(
            id, email, passwordHash, authProvider, externalId,
            profile, roles, permissions,
            true, active, lastLoginAt,
            createdAt, Instant.now()
        );
    }

    public User recordLogin() {
        return new User(
            id, email, passwordHash, authProvider, externalId,
            profile, roles, permissions,
            emailVerified, active, Instant.now(),
            createdAt, Instant.now()
        );
    }

    public User deactivate() {
        if (!active) {
            return this;
        }
        return new User(
            id, email, passwordHash, authProvider, externalId,
            profile, roles, permissions,
            emailVerified, false, lastLoginAt,
            createdAt, Instant.now()
        );
    }

    public User activate() {
        if (active) {
            return this;
        }
        return new User(
            id, email, passwordHash, authProvider, externalId,
            profile, roles, permissions,
            emailVerified, true, lastLoginAt,
            createdAt, Instant.now()
        );
    }

    // ===== Update methods (return new immutable instance) =====

    public User withPasswordHash(String newPasswordHash) {
        if (authProvider != AuthProvider.LOCAL) {
            throw new IllegalStateException("Cannot change password for external auth provider");
        }
        return new User(
            id, email, newPasswordHash, authProvider, externalId,
            profile, roles, permissions,
            emailVerified, active, lastLoginAt,
            createdAt, Instant.now()
        );
    }

    public User withProfile(UserProfile newProfile) {
        return new User(
            id, email, passwordHash, authProvider, externalId,
            newProfile, roles, permissions,
            emailVerified, active, lastLoginAt,
            createdAt, Instant.now()
        );
    }

    public User withRoles(Set<Role> newRoles) {
        Set<Permission> newPermissions = new HashSet<>();
        for (Role role : newRoles) {
            newPermissions.addAll(defaultPermissionsForRole(role));
        }
        return new User(
            id, email, passwordHash, authProvider, externalId,
            profile, newRoles, newPermissions,
            emailVerified, active, lastLoginAt,
            createdAt, Instant.now()
        );
    }

    public User addRole(Role role) {
        if (roles.contains(role)) {
            return this;
        }
        var newRoles = new HashSet<>(roles);
        newRoles.add(role);
        return withRoles(newRoles);
    }

    public User removeRole(Role role) {
        if (!roles.contains(role)) {
            return this;
        }
        var newRoles = new HashSet<>(roles);
        newRoles.remove(role);
        return withRoles(newRoles);
    }

    public User addPermission(Permission permission) {
        if (permissions.contains(permission)) {
            return this;
        }
        var newPermissions = new HashSet<>(permissions);
        newPermissions.add(permission);
        return new User(
            id, email, passwordHash, authProvider, externalId,
            profile, roles, newPermissions,
            emailVerified, active, lastLoginAt,
            createdAt, Instant.now()
        );
    }

    // ===== Query methods =====

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public boolean hasAnyRole(Role... checkRoles) {
        for (Role role : checkRoles) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllPermissions(Permission... checkPermissions) {
        for (Permission permission : checkPermissions) {
            if (!permissions.contains(permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    public boolean isPremium() {
        return hasRole(Role.PREMIUM);
    }

    public boolean canLogin() {
        return active && (authProvider != AuthProvider.LOCAL || emailVerified);
    }

    public boolean isLocalAuth() {
        return authProvider == AuthProvider.LOCAL;
    }

    private static Set<Permission> defaultPermissionsForRole(Role role) {
        return switch (role) {
            case USER -> Set.of(
                Permission.ITEM_CREATE,
                Permission.ITEM_READ,
                Permission.ITEM_UPDATE,
                Permission.ITEM_DELETE,
                Permission.DOMAIN_READ,
                Permission.USER_READ_OWN,
                Permission.USER_UPDATE_OWN
            );
            case PREMIUM -> {
                var permissions = new HashSet<>(defaultPermissionsForRole(Role.USER));
                permissions.add(Permission.EXPORT_DATA);
                permissions.add(Permission.IMPORT_DATA);
                yield permissions;
            }
            case ADMIN -> {
                var permissions = new HashSet<>(defaultPermissionsForRole(Role.PREMIUM));
                permissions.add(Permission.ADMIN_ACCESS);
                permissions.add(Permission.ADMIN_MANAGE_USERS);
                permissions.add(Permission.ADMIN_MANAGE_DOMAINS);
                permissions.add(Permission.DOMAIN_MANAGE);
                permissions.add(Permission.USER_READ_ANY);
                permissions.add(Permission.USER_UPDATE_ANY);
                permissions.add(Permission.USER_DELETE_ANY);
                yield permissions;
            }
            case SYSTEM -> Set.of(Permission.values());
        };
    }
}
