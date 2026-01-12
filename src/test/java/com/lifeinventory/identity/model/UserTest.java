package com.lifeinventory.identity.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("createLocal should create user with correct defaults")
    void createLocal_shouldCreateUserWithCorrectDefaults() {
        String email = "test@example.com";
        String passwordHash = "hashedPassword123";

        User user = User.createLocal(email, passwordHash);

        assertNotNull(user.id());
        assertEquals(email.toLowerCase(), user.email());
        assertEquals(passwordHash, user.passwordHash());
        assertEquals(AuthProvider.LOCAL, user.authProvider());
        assertNull(user.externalId());
        assertEquals(Set.of(Role.USER), user.roles());
        assertFalse(user.emailVerified());
        assertTrue(user.active());
        assertNull(user.lastLoginAt());
        assertNotNull(user.createdAt());
        assertNotNull(user.updatedAt());
    }

    @Test
    @DisplayName("createExternal should create user with verified email")
    void createExternal_shouldCreateUserWithVerifiedEmail() {
        String email = "test@example.com";
        String externalId = "google-123";
        UserProfile profile = UserProfile.ofName("John", "Doe");

        User user = User.createExternal(email, AuthProvider.GOOGLE, externalId, profile);

        assertEquals(email.toLowerCase(), user.email());
        assertNull(user.passwordHash());
        assertEquals(AuthProvider.GOOGLE, user.authProvider());
        assertEquals(externalId, user.externalId());
        assertTrue(user.emailVerified());
        assertEquals("John", user.profile().firstName());
        assertEquals("Doe", user.profile().lastName());
    }

    @Test
    @DisplayName("createLocal should fail without password hash")
    void createLocal_shouldFailWithoutPasswordHash() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(
                UUID.randomUUID(),
                "test@example.com",
                null,
                AuthProvider.LOCAL,
                null,
                UserProfile.empty(),
                Set.of(Role.USER),
                Set.of(),
                false,
                true,
                null,
                java.time.Instant.now(),
                java.time.Instant.now()
            )
        );
    }

    @Test
    @DisplayName("createExternal should fail without external ID")
    void createExternal_shouldFailWithoutExternalId() {
        assertThrows(IllegalArgumentException.class, () ->
            new User(
                UUID.randomUUID(),
                "test@example.com",
                null,
                AuthProvider.GOOGLE,
                null,
                UserProfile.empty(),
                Set.of(Role.USER),
                Set.of(),
                true,
                true,
                null,
                java.time.Instant.now(),
                java.time.Instant.now()
            )
        );
    }

    @Test
    @DisplayName("markEmailVerified should return user with verified email")
    void markEmailVerified_shouldReturnUserWithVerifiedEmail() {
        User user = User.createLocal("test@example.com", "hash");

        assertFalse(user.emailVerified());

        User verified = user.markEmailVerified();

        assertTrue(verified.emailVerified());
        assertEquals(user.id(), verified.id());
    }

    @Test
    @DisplayName("markEmailVerified should return same instance if already verified")
    void markEmailVerified_shouldReturnSameInstanceIfAlreadyVerified() {
        User user = User.createLocal("test@example.com", "hash").markEmailVerified();

        User result = user.markEmailVerified();

        assertSame(user, result);
    }

    @Test
    @DisplayName("recordLogin should update lastLoginAt")
    void recordLogin_shouldUpdateLastLoginAt() {
        User user = User.createLocal("test@example.com", "hash");

        assertNull(user.lastLoginAt());

        User loggedIn = user.recordLogin();

        assertNotNull(loggedIn.lastLoginAt());
    }

    @Test
    @DisplayName("deactivate should set active to false")
    void deactivate_shouldSetActiveToFalse() {
        User user = User.createLocal("test@example.com", "hash");

        assertTrue(user.active());

        User deactivated = user.deactivate();

        assertFalse(deactivated.active());
    }

    @Test
    @DisplayName("activate should set active to true")
    void activate_shouldSetActiveToTrue() {
        User user = User.createLocal("test@example.com", "hash").deactivate();

        assertFalse(user.active());

        User activated = user.activate();

        assertTrue(activated.active());
    }

    @Test
    @DisplayName("withPasswordHash should update password")
    void withPasswordHash_shouldUpdatePassword() {
        User user = User.createLocal("test@example.com", "oldHash");
        String newHash = "newHash";

        User updated = user.withPasswordHash(newHash);

        assertEquals(newHash, updated.passwordHash());
        assertEquals(user.id(), updated.id());
    }

    @Test
    @DisplayName("withPasswordHash should fail for external auth")
    void withPasswordHash_shouldFailForExternalAuth() {
        User user = User.createExternal(
            "test@example.com",
            AuthProvider.GOOGLE,
            "google-123",
            UserProfile.empty()
        );

        assertThrows(IllegalStateException.class, () ->
            user.withPasswordHash("newHash")
        );
    }

    @Test
    @DisplayName("withProfile should update profile")
    void withProfile_shouldUpdateProfile() {
        User user = User.createLocal("test@example.com", "hash");
        UserProfile newProfile = UserProfile.ofName("Jane", "Doe");

        User updated = user.withProfile(newProfile);

        assertEquals("Jane", updated.profile().firstName());
        assertEquals("Doe", updated.profile().lastName());
    }

    @Test
    @DisplayName("addRole should add new role with permissions")
    void addRole_shouldAddNewRoleWithPermissions() {
        User user = User.createLocal("test@example.com", "hash");

        assertFalse(user.hasRole(Role.PREMIUM));

        User premium = user.addRole(Role.PREMIUM);

        assertTrue(premium.hasRole(Role.USER));
        assertTrue(premium.hasRole(Role.PREMIUM));
        assertTrue(premium.hasPermission(Permission.EXPORT_DATA));
    }

    @Test
    @DisplayName("removeRole should remove role")
    void removeRole_shouldRemoveRole() {
        User user = User.createLocal("test@example.com", "hash")
            .addRole(Role.PREMIUM);

        assertTrue(user.hasRole(Role.PREMIUM));

        User updated = user.removeRole(Role.PREMIUM);

        assertFalse(updated.hasRole(Role.PREMIUM));
        assertTrue(updated.hasRole(Role.USER));
    }

    @Test
    @DisplayName("canLogin should return true for active verified local user")
    void canLogin_shouldReturnTrueForActiveVerifiedLocalUser() {
        User user = User.createLocal("test@example.com", "hash")
            .markEmailVerified();

        assertTrue(user.canLogin());
    }

    @Test
    @DisplayName("canLogin should return false for unverified local user")
    void canLogin_shouldReturnFalseForUnverifiedLocalUser() {
        User user = User.createLocal("test@example.com", "hash");

        assertFalse(user.canLogin());
    }

    @Test
    @DisplayName("canLogin should return false for inactive user")
    void canLogin_shouldReturnFalseForInactiveUser() {
        User user = User.createLocal("test@example.com", "hash")
            .markEmailVerified()
            .deactivate();

        assertFalse(user.canLogin());
    }

    @Test
    @DisplayName("canLogin should return true for external auth user")
    void canLogin_shouldReturnTrueForExternalAuthUser() {
        User user = User.createExternal(
            "test@example.com",
            AuthProvider.GOOGLE,
            "google-123",
            UserProfile.empty()
        );

        assertTrue(user.canLogin());
    }

    @Test
    @DisplayName("isAdmin should return true for admin user")
    void isAdmin_shouldReturnTrueForAdminUser() {
        User user = User.createLocal("test@example.com", "hash")
            .addRole(Role.ADMIN);

        assertTrue(user.isAdmin());
    }

    @Test
    @DisplayName("hasAllPermissions should return true when user has all permissions")
    void hasAllPermissions_shouldReturnTrueWhenUserHasAllPermissions() {
        User user = User.createLocal("test@example.com", "hash");

        assertTrue(user.hasAllPermissions(Permission.ITEM_CREATE, Permission.ITEM_READ));
    }

    @Test
    @DisplayName("hasAllPermissions should return false when user lacks permission")
    void hasAllPermissions_shouldReturnFalseWhenUserLacksPermission() {
        User user = User.createLocal("test@example.com", "hash");

        assertFalse(user.hasAllPermissions(Permission.ITEM_CREATE, Permission.ADMIN_ACCESS));
    }
}
