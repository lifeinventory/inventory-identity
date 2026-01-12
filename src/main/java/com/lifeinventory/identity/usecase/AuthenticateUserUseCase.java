package com.lifeinventory.identity.usecase;

import com.lifeinventory.identity.model.AuthProvider;
import com.lifeinventory.identity.model.AuthenticationResult;
import com.lifeinventory.identity.model.Credentials;
import lombok.NonNull;

/**
 * Use case for authenticating a user.
 */
public interface AuthenticateUserUseCase {

    /**
     * Authenticate a user with credentials.
     *
     * @param command authentication data
     * @return authentication result with user and tokens
     * @throws com.lifeinventory.identity.exception.InvalidCredentialsException if credentials are invalid
     * @throws com.lifeinventory.identity.exception.UserNotFoundException if user does not exist
     * @throws com.lifeinventory.identity.exception.UserNotActiveException if user is deactivated
     */
    AuthenticationResult execute(AuthenticateCommand command);

    /**
     * Command for authentication.
     */
    record AuthenticateCommand(
        @NonNull AuthProvider provider,
        String email,
        String password,
        String externalToken,
        String ipAddress,
        String userAgent
    ) {
        public AuthenticateCommand {
            if (provider == AuthProvider.LOCAL) {
                if (email == null || email.isBlank()) {
                    throw new IllegalArgumentException("email is required for local authentication");
                }
                if (password == null || password.isBlank()) {
                    throw new IllegalArgumentException("password is required for local authentication");
                }
            } else {
                if (externalToken == null || externalToken.isBlank()) {
                    throw new IllegalArgumentException("externalToken is required for external authentication");
                }
            }
        }

        /**
         * Create a local authentication command.
         */
        public static AuthenticateCommand local(String email, String password) {
            return new AuthenticateCommand(AuthProvider.LOCAL, email, password, null, null, null);
        }

        /**
         * Create a local authentication command with metadata.
         */
        public static AuthenticateCommand local(String email, String password, String ipAddress, String userAgent) {
            return new AuthenticateCommand(AuthProvider.LOCAL, email, password, null, ipAddress, userAgent);
        }

        /**
         * Create a local authentication command from Credentials.
         */
        public static AuthenticateCommand local(Credentials credentials) {
            return local(credentials.email(), credentials.password());
        }

        /**
         * Create an external authentication command.
         */
        public static AuthenticateCommand external(AuthProvider provider, String externalToken) {
            return new AuthenticateCommand(provider, null, null, externalToken, null, null);
        }

        /**
         * Create an external authentication command with metadata.
         */
        public static AuthenticateCommand external(AuthProvider provider, String externalToken, String ipAddress, String userAgent) {
            return new AuthenticateCommand(provider, null, null, externalToken, ipAddress, userAgent);
        }

        public boolean isLocalAuthentication() {
            return provider == AuthProvider.LOCAL;
        }
    }
}
