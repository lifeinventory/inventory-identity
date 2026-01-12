package com.lifeinventory.identity.repository;

import com.lifeinventory.identity.model.AuthProvider;
import com.lifeinventory.identity.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User persistence.
 * Output port in hexagonal architecture.
 */
public interface UserRepository {

    /**
     * Save a user (create or update).
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Find user by ID.
     *
     * @param userId the user ID
     * @return the user if found
     */
    Optional<User> findById(UUID userId);

    /**
     * Find user by email.
     *
     * @param email the email address
     * @return the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by external auth provider and external ID.
     *
     * @param provider the auth provider
     * @param externalId the external ID from the provider
     * @return the user if found
     */
    Optional<User> findByProviderAndExternalId(AuthProvider provider, String externalId);

    /**
     * Find all users with pagination.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return list of users
     */
    List<User> findAll(int page, int size);

    /**
     * Find all active users with pagination.
     *
     * @param page page number (0-based)
     * @param size page size
     * @return list of active users
     */
    List<User> findAllActive(int page, int size);

    /**
     * Delete user by ID.
     *
     * @param userId the user ID
     */
    void deleteById(UUID userId);

    /**
     * Check if user exists by ID.
     *
     * @param userId the user ID
     * @return true if user exists
     */
    boolean existsById(UUID userId);

    /**
     * Check if user exists by email.
     *
     * @param email the email address
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Count all users.
     *
     * @return total user count
     */
    long count();

    /**
     * Count active users.
     *
     * @return active user count
     */
    long countActive();
}
