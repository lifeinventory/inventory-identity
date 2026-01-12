package com.lifeinventory.identity.model;

/**
 * Supported authentication providers.
 */
public enum AuthProvider {
    /**
     * Local email/password authentication.
     */
    LOCAL,

    /**
     * Google OAuth2.
     */
    GOOGLE,

    /**
     * Apple Sign In.
     */
    APPLE,

    /**
     * Facebook OAuth2.
     */
    FACEBOOK
}
