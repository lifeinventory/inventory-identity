package com.lifeinventory.identity.usecase;

import com.lifeinventory.identity.model.User;
import lombok.NonNull;

/**
 * Use case for verifying a user's email address.
 */
public interface VerifyEmailUseCase {

    /**
     * Verify user email using a verification token.
     *
     * @param command verification data
     * @return the updated user with verified email
     * @throws com.lifeinventory.identity.exception.InvalidTokenException if token is invalid
     * @throws com.lifeinventory.identity.exception.TokenExpiredException if token is expired
     */
    User execute(VerifyEmailCommand command);

    /**
     * Command for email verification.
     */
    record VerifyEmailCommand(
        @NonNull String token
    ) {
        public VerifyEmailCommand {
            if (token.isBlank()) {
                throw new IllegalArgumentException("token must not be blank");
            }
        }

        public static VerifyEmailCommand of(String token) {
            return new VerifyEmailCommand(token);
        }
    }
}
