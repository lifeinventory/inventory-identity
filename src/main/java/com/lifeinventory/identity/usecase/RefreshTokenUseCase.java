package com.lifeinventory.identity.usecase;

import com.lifeinventory.identity.model.AuthenticationResult;
import lombok.NonNull;

/**
 * Use case for refreshing an access token.
 */
public interface RefreshTokenUseCase {

    /**
     * Refresh an access token using a refresh token.
     *
     * @param command refresh data
     * @return new authentication result with fresh tokens
     * @throws com.lifeinventory.identity.exception.InvalidTokenException if refresh token is invalid
     * @throws com.lifeinventory.identity.exception.TokenExpiredException if refresh token is expired
     */
    AuthenticationResult execute(RefreshCommand command);

    /**
     * Command for token refresh.
     */
    record RefreshCommand(
        @NonNull String refreshToken
    ) {
        public RefreshCommand {
            if (refreshToken.isBlank()) {
                throw new IllegalArgumentException("refreshToken must not be blank");
            }
        }

        public static RefreshCommand of(String refreshToken) {
            return new RefreshCommand(refreshToken);
        }
    }
}
