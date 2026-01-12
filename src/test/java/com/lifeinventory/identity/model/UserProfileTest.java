package com.lifeinventory.identity.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    @Test
    @DisplayName("empty should create profile with defaults")
    void empty_shouldCreateProfileWithDefaults() {
        UserProfile profile = UserProfile.empty();

        assertNull(profile.displayName());
        assertNull(profile.firstName());
        assertNull(profile.lastName());
        assertNull(profile.avatarUrl());
        assertEquals("en", profile.locale());
        assertEquals("UTC", profile.timezone());
    }

    @Test
    @DisplayName("withDisplayName should create profile with display name")
    void withDisplayName_shouldCreateProfileWithDisplayName() {
        UserProfile profile = UserProfile.ofDisplayName("Johnny");

        assertEquals("Johnny", profile.displayName());
        assertNull(profile.firstName());
        assertNull(profile.lastName());
    }

    @Test
    @DisplayName("withName should create profile with full name")
    void withName_shouldCreateProfileWithFullName() {
        UserProfile profile = UserProfile.ofName("John", "Doe");

        assertEquals("John Doe", profile.displayName());
        assertEquals("John", profile.firstName());
        assertEquals("Doe", profile.lastName());
    }

    @Test
    @DisplayName("withName should handle null first name")
    void withName_shouldHandleNullFirstName() {
        UserProfile profile = UserProfile.ofName(null, "Doe");

        assertEquals("Doe", profile.displayName());
        assertNull(profile.firstName());
        assertEquals("Doe", profile.lastName());
    }

    @Test
    @DisplayName("withName should handle null last name")
    void withName_shouldHandleNullLastName() {
        UserProfile profile = UserProfile.ofName("John", null);

        assertEquals("John", profile.displayName());
        assertEquals("John", profile.firstName());
        assertNull(profile.lastName());
    }

    @Test
    @DisplayName("getFullName should return constructed full name")
    void getFullName_shouldReturnConstructedFullName() {
        UserProfile profile = UserProfile.ofName("John", "Doe");

        assertEquals("John Doe", profile.getFullName());
    }

    @Test
    @DisplayName("getFullName should return display name when no first/last name")
    void getFullName_shouldReturnDisplayNameWhenNoFirstLastName() {
        UserProfile profile = UserProfile.ofDisplayName("Johnny");

        assertEquals("Johnny", profile.getFullName());
    }

    @Test
    @DisplayName("withAvatarUrl should update avatar")
    void withAvatarUrl_shouldUpdateAvatar() {
        UserProfile profile = UserProfile.empty();
        String avatarUrl = "https://example.com/avatar.jpg";

        UserProfile updated = profile.withAvatarUrl(avatarUrl);

        assertEquals(avatarUrl, updated.avatarUrl());
        assertEquals(profile.locale(), updated.locale());
    }

    @Test
    @DisplayName("withLocale should update locale")
    void withLocale_shouldUpdateLocale() {
        UserProfile profile = UserProfile.empty();

        UserProfile updated = profile.withLocale("ru");

        assertEquals("ru", updated.locale());
        assertEquals(profile.timezone(), updated.timezone());
    }

    @Test
    @DisplayName("withTimezone should update timezone")
    void withTimezone_shouldUpdateTimezone() {
        UserProfile profile = UserProfile.empty();

        UserProfile updated = profile.withTimezone("Europe/Minsk");

        assertEquals("Europe/Minsk", updated.timezone());
        assertEquals(profile.locale(), updated.locale());
    }

    @Test
    @DisplayName("update methods should be chainable")
    void updateMethods_shouldBeChainable() {
        UserProfile profile = UserProfile.empty()
            .withName("John", "Doe")
            .withAvatarUrl("https://example.com/avatar.jpg")
            .withLocale("de")
            .withTimezone("Europe/Berlin");

        assertEquals("John", profile.firstName());
        assertEquals("Doe", profile.lastName());
        assertEquals("https://example.com/avatar.jpg", profile.avatarUrl());
        assertEquals("de", profile.locale());
        assertEquals("Europe/Berlin", profile.timezone());
    }

    @Test
    @DisplayName("constructor should default null locale to en")
    void constructor_shouldDefaultNullLocaleToEn() {
        UserProfile profile = new UserProfile("Name", null, null, null, null, null);

        assertEquals("en", profile.locale());
    }

    @Test
    @DisplayName("constructor should default null timezone to UTC")
    void constructor_shouldDefaultNullTimezoneToUtc() {
        UserProfile profile = new UserProfile("Name", null, null, null, null, null);

        assertEquals("UTC", profile.timezone());
    }
}
