package com.lifeinventory.identity.usecase;

import com.lifeinventory.identity.model.AuthProvider;
import com.lifeinventory.identity.model.User;
import com.lifeinventory.identity.model.UserProfile;
import lombok.NonNull;

import java.util.Objects;

/**
 * Use case for registering a new user.
 */
public interface RegisterUserUseCase {

    /**
     * Register a new user.
     *
     * @param command registration data
     * @return the created user
     * @throws com.lifeinventory.identity.exception.UserAlreadyExistsException if email is already registered
     */
    User execute(RegisterCommand command);

    /**
     * Command for local registration (email/password).
     */
    record RegisterCommand(
        @NonNull String email,
        @NonNull String password,
        @NonNull AuthProvider provider,
        String externalId,
        UserProfile profile
    ) {
        public RegisterCommand {
            if (email.isBlank()) {
                throw new IllegalArgumentException("email must not be blank");
            }
            if (provider == AuthProvider.LOCAL && (password == null || password.isBlank())) {
                throw new IllegalArgumentException("password is required for local registration");
            }
            if (provider != AuthProvider.LOCAL && (externalId == null || externalId.isBlank())) {
                throw new IllegalArgumentException("externalId is required for external provider registration");
            }
            profile = Objects.requireNonNullElseGet(profile, UserProfile::empty);
        }

        /**
         * Create a local registration command.
         */
        public static RegisterCommand local(String email, String password) {
            return new RegisterCommand(email, password, AuthProvider.LOCAL, null, null);
        }

        /**
         * Create a local registration command with profile.
         */
        public static RegisterCommand local(String email, String password, UserProfile profile) {
            return new RegisterCommand(email, password, AuthProvider.LOCAL, null, profile);
        }

        /**
         * Create an external provider registration command.
         */
        public static RegisterCommand external(String email, AuthProvider provider, String externalId, UserProfile profile) {
            return new RegisterCommand(email, "", provider, externalId, profile);
        }

        public boolean isLocalRegistration() {
            return provider == AuthProvider.LOCAL;
        }
    }
}
