package com.lifeinventory.identity.service;

/**
 * Interface for password hashing operations.
 * Output port - implementation provided by infrastructure layer.
 */
public interface PasswordHasher {

    /**
     * Hash a plain text password.
     *
     * @param plainPassword the plain text password
     * @return the hashed password
     */
    String hash(String plainPassword);

    /**
     * Verify a plain text password against a hash.
     *
     * @param plainPassword the plain text password to verify
     * @param hashedPassword the stored hash to verify against
     * @return true if the password matches the hash
     */
    boolean verify(String plainPassword, String hashedPassword);
}
