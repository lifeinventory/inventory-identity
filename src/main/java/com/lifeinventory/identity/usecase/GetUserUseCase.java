package com.lifeinventory.identity.usecase;

import com.lifeinventory.identity.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case for retrieving user information.
 */
public interface GetUserUseCase {

    /**
     * Get user by ID.
     *
     * @param userId the user ID
     * @return the user if found
     */
    Optional<User> getById(UUID userId);

    /**
     * Get user by email.
     *
     * @param email the email address
     * @return the user if found
     */
    Optional<User> getByEmail(String email);

    /**
     * Get user by external provider and external ID.
     *
     * @param provider the auth provider
     * @param externalId the external ID
     * @return the user if found
     */
    Optional<User> getByExternalId(String provider, String externalId);

    /**
     * Get all users (admin only).
     *
     * @param page page number (0-based)
     * @param size page size
     * @return list of users
     */
    List<User> getAll(int page, int size);

    /**
     * Count all users.
     *
     * @return total user count
     */
    long count();
}
