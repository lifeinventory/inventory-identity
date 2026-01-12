package com.lifeinventory.identity.usecase;

import com.lifeinventory.identity.model.Token;
import lombok.NonNull;

import java.util.Optional;

/**
 * Use case for requesting a password reset.
 */
public interface RequestPasswordResetUseCase {

    /**
     * Request a password reset for a user.
     * Returns empty if user doesn't exist (to prevent user enumeration).
     *
     * @param command request data
     * @return the password reset token if user exists
     */
    Optional<Token> execute(RequestPasswordResetCommand command);

    /**
     * Command for password reset request.
     */
    record RequestPasswordResetCommand(
        @NonNull String email
    ) {
        public RequestPasswordResetCommand {
            if (email.isBlank()) {
                throw new IllegalArgumentException("email must not be blank");
            }
        }

        public static RequestPasswordResetCommand of(String email) {
            return new RequestPasswordResetCommand(email);
        }

        public String normalizedEmail() {
            return email.toLowerCase().trim();
        }
    }
}
