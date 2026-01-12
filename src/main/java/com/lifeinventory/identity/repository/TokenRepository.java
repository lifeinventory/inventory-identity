package com.lifeinventory.identity.repository;

import com.lifeinventory.identity.model.Token;
import com.lifeinventory.identity.model.TokenType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Token persistence.
 * Output port in hexagonal architecture.
 */
public interface TokenRepository {

    /**
     * Save a token.
     *
     * @param token the token to save
     * @return the saved token
     */
    Token save(Token token);

    /**
     * Find token by ID.
     *
     * @param tokenId the token ID
     * @return the token if found
     */
    Optional<Token> findById(UUID tokenId);

    /**
     * Find token by token value.
     *
     * @param tokenValue the token value string
     * @return the token if found
     */
    Optional<Token> findByTokenValue(String tokenValue);

    /**
     * Find token by token value and type.
     *
     * @param tokenValue the token value string
     * @param type the token type
     * @return the token if found
     */
    Optional<Token> findByTokenValueAndType(String tokenValue, TokenType type);

    /**
     * Find all tokens for a user.
     *
     * @param userId the user ID
     * @return list of tokens
     */
    List<Token> findByUserId(UUID userId);

    /**
     * Find all tokens for a user by type.
     *
     * @param userId the user ID
     * @param type the token type
     * @return list of tokens
     */
    List<Token> findByUserIdAndType(UUID userId, TokenType type);

    /**
     * Find all valid (not revoked, not expired) tokens for a user.
     *
     * @param userId the user ID
     * @return list of valid tokens
     */
    List<Token> findValidTokensByUserId(UUID userId);

    /**
     * Delete token by ID.
     *
     * @param tokenId the token ID
     */
    void deleteById(UUID tokenId);

    /**
     * Delete token by token value.
     *
     * @param tokenValue the token value
     */
    void deleteByTokenValue(String tokenValue);

    /**
     * Delete all tokens for a user.
     *
     * @param userId the user ID
     */
    void deleteAllByUserId(UUID userId);

    /**
     * Delete all tokens for a user by type.
     *
     * @param userId the user ID
     * @param type the token type
     */
    void deleteAllByUserIdAndType(UUID userId, TokenType type);

    /**
     * Delete all expired tokens.
     * Used for cleanup.
     *
     * @return number of deleted tokens
     */
    int deleteAllExpired();

    /**
     * Revoke all tokens for a user.
     *
     * @param userId the user ID
     * @return number of revoked tokens
     */
    int revokeAllByUserId(UUID userId);

    /**
     * Revoke all refresh tokens for a user.
     *
     * @param userId the user ID
     * @return number of revoked tokens
     */
    int revokeAllRefreshTokensByUserId(UUID userId);
}
