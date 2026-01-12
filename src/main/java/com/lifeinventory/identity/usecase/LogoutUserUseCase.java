package com.lifeinventory.identity.usecase;

import lombok.NonNull;

import java.util.UUID;

/**
 * Use case for logging out a user.
 */
public interface LogoutUserUseCase {

    /**
     * Logout a user by invalidating their tokens.
     *
     * @param command logout data
     */
    void execute(LogoutCommand command);

    /**
     * Command for logout.
     */
    record LogoutCommand(
        @NonNull UUID userId,
        String refreshToken,
        boolean logoutAllDevices
    ) {
        public LogoutCommand {
            // At least one of refreshToken or logoutAllDevices must be specified
        }

        /**
         * Logout from current device only.
         */
        public static LogoutCommand single(UUID userId, String refreshToken) {
            return new LogoutCommand(userId, refreshToken, false);
        }

        /**
         * Logout from all devices.
         */
        public static LogoutCommand allDevices(UUID userId) {
            return new LogoutCommand(userId, null, true);
        }
    }
}
