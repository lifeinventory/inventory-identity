package com.lifeinventory.identity.service;

import com.lifeinventory.identity.model.Token;
import com.lifeinventory.identity.model.User;

/**
 * Interface for generating authentication tokens.
 * Output port - implementation provided by infrastructure layer.
 */
public interface TokenGenerator {

    /**
     * Generate an access token for a user.
     *
     * @param user the user
     * @return the access token
     */
    Token generateAccessToken(User user);

    /**
     * Generate a refresh token for a user.
     *
     * @param user the user
     * @return the refresh token
     */
    Token generateRefreshToken(User user);

    /**
     * Generate a password reset token for a user.
     *
     * @param user the user
     * @return the password reset token
     */
    Token generatePasswordResetToken(User user);

    /**
     * Generate an email verification token for a user.
     *
     * @param user the user
     * @return the email verification token
     */
    Token generateEmailVerificationToken(User user);
}
