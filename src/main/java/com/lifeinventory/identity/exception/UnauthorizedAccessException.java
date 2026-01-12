package com.lifeinventory.identity.exception;

import java.util.UUID;

/**
 * Exception thrown when a user attempts to access a resource they are not authorized for.
 */
public class UnauthorizedAccessException extends RuntimeException {

    private final UUID requesterId;
    private final UUID resourceId;
    private final String resourceType;

    public UnauthorizedAccessException(UUID requesterId, UUID resourceId, String resourceType, String message) {
        super(message);
        this.requesterId = requesterId;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }

    public static UnauthorizedAccessException forUser(UUID requesterId, UUID targetUserId) {
        return new UnauthorizedAccessException(
            requesterId,
            targetUserId,
            "User",
            "User " + requesterId + " is not authorized to access user " + targetUserId
        );
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }
}
