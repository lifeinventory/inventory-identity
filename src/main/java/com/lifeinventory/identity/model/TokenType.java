package com.lifeinventory.identity.model;

/**
 * Types of tokens used in the authentication flow.
 */
public enum TokenType {
    /**
     * Short-lived access token for API requests.
     */
    ACCESS,

    /**
     * Long-lived token for obtaining new access tokens.
     */
    REFRESH,

    /**
     * Token for password reset flow.
     */
    PASSWORD_RESET,

    /**
     * Token for email verification.
     */
    EMAIL_VERIFICATION
}
