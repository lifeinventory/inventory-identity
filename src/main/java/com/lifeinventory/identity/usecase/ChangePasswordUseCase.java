package com.lifeinventory.identity.usecase;

import com.lifeinventory.identity.model.User;
import lombok.NonNull;

import java.util.UUID;

/**
 * Use case for changing a user's password (when they know their current password).
 */
public interface ChangePasswordUseCase {

    /**
     * Change user password.
     *
     * @param command change data
     * @return the updated user
     * @throws com.lifeinventory.identity.exception.UserNotFoundException if user does not exist
     * @throws com.lifeinventory.identity.exception.InvalidCredentialsException if current password is wrong
     */
    User execute(ChangePasswordCommand command);

    /**
     * Command for password change.
     */
    record ChangePasswordCommand(
        @NonNull UUID userId,
        @NonNull String currentPassword,
        @NonNull String newPassword
    ) {
        public ChangePasswordCommand {
            if (currentPassword.isBlank()) {
                throw new IllegalArgumentException("currentPassword must not be blank");
            }
            if (newPassword.isBlank()) {
                throw new IllegalArgumentException("newPassword must not be blank");
            }
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("newPassword must be at least 8 characters");
            }
            if (currentPassword.equals(newPassword)) {
                throw new IllegalArgumentException("newPassword must be different from currentPassword");
            }
        }

        public static ChangePasswordCommand of(UUID userId, String currentPassword, String newPassword) {
            return new ChangePasswordCommand(userId, currentPassword, newPassword);
        }
    }
}
