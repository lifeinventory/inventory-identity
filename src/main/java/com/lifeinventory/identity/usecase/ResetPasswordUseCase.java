package com.lifeinventory.identity.usecase;

import com.lifeinventory.identity.model.User;
import lombok.NonNull;

/**
 * Use case for resetting a user's password.
 */
public interface ResetPasswordUseCase {

    /**
     * Reset user password using a password reset token.
     *
     * @param command reset data
     * @return the updated user
     * @throws com.lifeinventory.identity.exception.InvalidTokenException if token is invalid
     * @throws com.lifeinventory.identity.exception.TokenExpiredException if token is expired
     */
    User execute(ResetPasswordCommand command);

    /**
     * Command for password reset.
     */
    record ResetPasswordCommand(
        @NonNull String token,
        @NonNull String newPassword
    ) {
        public ResetPasswordCommand {
            if (token.isBlank()) {
                throw new IllegalArgumentException("token must not be blank");
            }
            if (newPassword.isBlank()) {
                throw new IllegalArgumentException("newPassword must not be blank");
            }
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("newPassword must be at least 8 characters");
            }
        }

        public static ResetPasswordCommand of(String token, String newPassword) {
            return new ResetPasswordCommand(token, newPassword);
        }
    }
}
