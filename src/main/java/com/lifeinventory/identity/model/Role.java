package com.lifeinventory.identity.model;

/**
 * User roles within the platform.
 */
public enum Role {
    /**
     * Regular user with access to own inventory.
     */
    USER,

    /**
     * Premium user with extended features.
     */
    PREMIUM,

    /**
     * Administrator with management capabilities.
     */
    ADMIN,

    /**
     * System role for internal operations.
     */
    SYSTEM
}
