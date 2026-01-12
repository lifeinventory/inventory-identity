package com.lifeinventory.identity.usecase;

import com.lifeinventory.identity.model.User;
import com.lifeinventory.identity.model.UserProfile;
import lombok.NonNull;

import java.util.UUID;

/**
 * Use case for updating user profile.
 */
public interface UpdateUserProfileUseCase {

    /**
     * Update user profile.
     *
     * @param command update data
     * @return the updated user
     * @throws com.lifeinventory.identity.exception.UserNotFoundException if user does not exist
     * @throws com.lifeinventory.identity.exception.UnauthorizedAccessException if requester is not authorized
     */
    User execute(UpdateProfileCommand command);

    /**
     * Command for updating profile.
     */
    record UpdateProfileCommand(
        @NonNull UUID userId,
        @NonNull UUID requesterId,
        String displayName,
        String firstName,
        String lastName,
        String avatarUrl,
        String locale,
        String timezone
    ) {
        /**
         * Apply updates to an existing profile.
         */
        public UserProfile applyTo(UserProfile existing) {
            var result = existing;

            if (displayName != null) {
                result = result.withDisplayName(displayName);
            }
            if (firstName != null || lastName != null) {
                String newFirst = firstName != null ? firstName : existing.firstName();
                String newLast = lastName != null ? lastName : existing.lastName();
                result = result.withName(newFirst, newLast);
            }
            if (avatarUrl != null) {
                result = result.withAvatarUrl(avatarUrl);
            }
            if (locale != null) {
                result = result.withLocale(locale);
            }
            if (timezone != null) {
                result = result.withTimezone(timezone);
            }

            return result;
        }

        public static Builder builder(UUID userId, UUID requesterId) {
            return new Builder(userId, requesterId);
        }

        public static class Builder {
            private final UUID userId;
            private final UUID requesterId;
            private String displayName;
            private String firstName;
            private String lastName;
            private String avatarUrl;
            private String locale;
            private String timezone;

            private Builder(UUID userId, UUID requesterId) {
                this.userId = userId;
                this.requesterId = requesterId;
            }

            public Builder displayName(String displayName) {
                this.displayName = displayName;
                return this;
            }

            public Builder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }

            public Builder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }

            public Builder avatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
                return this;
            }

            public Builder locale(String locale) {
                this.locale = locale;
                return this;
            }

            public Builder timezone(String timezone) {
                this.timezone = timezone;
                return this;
            }

            public UpdateProfileCommand build() {
                return new UpdateProfileCommand(
                    userId, requesterId,
                    displayName, firstName, lastName,
                    avatarUrl, locale, timezone
                );
            }
        }
    }
}
