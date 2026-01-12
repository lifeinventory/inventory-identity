package com.lifeinventory.identity.model;

import lombok.NonNull;

import java.util.Objects;

/**
 * User profile information.
 * Immutable value object.
 */
public record UserProfile(
    String displayName,
    String firstName,
    String lastName,
    String avatarUrl,
    String locale,
    String timezone
) {
    public UserProfile {
        locale = Objects.requireNonNullElse(locale, "en");
        timezone = Objects.requireNonNullElse(timezone, "UTC");
    }

    /**
     * Create an empty profile with defaults.
     */
    public static UserProfile empty() {
        return new UserProfile(null, null, null, null, "en", "UTC");
    }

    /**
     * Create a profile with display name only.
     */
    public static UserProfile ofDisplayName(String displayName) {
        return new UserProfile(displayName, null, null, null, "en", "UTC");
    }

    /**
     * Create a profile with full name.
     */
    public static UserProfile ofName(String firstName, String lastName) {
        String displayName = buildDisplayName(firstName, lastName);
        return new UserProfile(displayName, firstName, lastName, null, "en", "UTC");
    }

    public UserProfile withDisplayName(String newDisplayName) {
        return new UserProfile(newDisplayName, firstName, lastName, avatarUrl, locale, timezone);
    }

    public UserProfile withName(String newFirstName, String newLastName) {
        String newDisplayName = buildDisplayName(newFirstName, newLastName);
        return new UserProfile(newDisplayName, newFirstName, newLastName, avatarUrl, locale, timezone);
    }

    public UserProfile withAvatarUrl(String newAvatarUrl) {
        return new UserProfile(displayName, firstName, lastName, newAvatarUrl, locale, timezone);
    }

    public UserProfile withLocale(String newLocale) {
        return new UserProfile(displayName, firstName, lastName, avatarUrl, newLocale, timezone);
    }

    public UserProfile withTimezone(String newTimezone) {
        return new UserProfile(displayName, firstName, lastName, avatarUrl, locale, newTimezone);
    }

    public String getFullName() {
        if (firstName == null && lastName == null) {
            return displayName;
        }
        return buildDisplayName(firstName, lastName);
    }

    private static String buildDisplayName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
