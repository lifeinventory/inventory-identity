package com.lifeinventory.identity.model;

/**
 * Fine-grained permissions for authorization.
 */
public enum Permission {
    // Item permissions
    ITEM_CREATE,
    ITEM_READ,
    ITEM_UPDATE,
    ITEM_DELETE,

    // Domain permissions
    DOMAIN_READ,
    DOMAIN_MANAGE,

    // User management permissions
    USER_READ_OWN,
    USER_UPDATE_OWN,
    USER_DELETE_OWN,
    USER_READ_ANY,
    USER_UPDATE_ANY,
    USER_DELETE_ANY,

    // Admin permissions
    ADMIN_ACCESS,
    ADMIN_MANAGE_USERS,
    ADMIN_MANAGE_DOMAINS,

    // Export/Import
    EXPORT_DATA,
    IMPORT_DATA
}
