package com.lifeinventory.identity.infrastructure.persistence.cassandra.adapter;

import com.lifeinventory.identity.infrastructure.persistence.cassandra.entity.TokenEntity;
import com.lifeinventory.identity.infrastructure.persistence.cassandra.mapper.EntityMapper;
import com.lifeinventory.identity.infrastructure.persistence.cassandra.repository.CassandraTokenRepository;
import com.lifeinventory.identity.model.Token;
import com.lifeinventory.identity.model.TokenType;
import com.lifeinventory.identity.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenRepositoryAdapter implements TokenRepository {

    private final CassandraTokenRepository cassandraRepository;
    private final EntityMapper mapper;

    @Override
    public Token save(Token token) {
        var entity = mapper.toEntity(token);
        return cassandraRepository.save(entity)
                .map(mapper::toDomain)
                .block();
    }

    @Override
    public Optional<Token> findById(UUID tokenId) {
        return cassandraRepository.findById(tokenId)
                .map(mapper::toDomain)
                .blockOptional();
    }

    @Override
    public Optional<Token> findByTokenValue(String tokenValue) {
        return cassandraRepository.findByTokenValue(tokenValue)
                .map(mapper::toDomain)
                .blockOptional();
    }

    @Override
    public Optional<Token> findByTokenValueAndType(String tokenValue, TokenType type) {
        return cassandraRepository.findByTokenValueAndTokenType(tokenValue, type.name())
                .map(mapper::toDomain)
                .blockOptional();
    }

    @Override
    public List<Token> findByUserId(UUID userId) {
        return cassandraRepository.findByUserId(userId)
                .map(mapper::toDomain)
                .collectList()
                .block();
    }

    @Override
    public List<Token> findByUserIdAndType(UUID userId, TokenType type) {
        return cassandraRepository.findByUserIdAndTokenType(userId, type.name())
                .map(mapper::toDomain)
                .collectList()
                .block();
    }

    @Override
    public List<Token> findValidTokensByUserId(UUID userId) {
        return cassandraRepository.findByUserId(userId)
                .map(mapper::toDomain)
                .filter(Token::isValid)
                .collectList()
                .block();
    }

    @Override
    public void deleteById(UUID tokenId) {
        cassandraRepository.deleteById(tokenId).block();
    }

    @Override
    public void deleteByTokenValue(String tokenValue) {
        cassandraRepository.deleteByTokenValue(tokenValue).block();
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        cassandraRepository.deleteAllByUserId(userId).block();
    }

    @Override
    public void deleteAllByUserIdAndType(UUID userId, TokenType type) {
        cassandraRepository.deleteAllByUserIdAndTokenType(userId, type.name()).block();
    }

    @Override
    public int deleteAllExpired() {
        // Find and delete all expired tokens
        var expired = cassandraRepository.findAll()
                .filter(entity -> entity.getExpiresAt().isBefore(Instant.now()))
                .collectList()
                .block();

        if (expired != null && !expired.isEmpty()) {
            for (TokenEntity entity : expired) {
                cassandraRepository.deleteById(entity.getId()).block();
            }
            return expired.size();
        }
        return 0;
    }

    @Override
    public int revokeAllByUserId(UUID userId) {
        var tokens = cassandraRepository.findByUserId(userId)
                .filter(entity -> !entity.isRevoked())
                .collectList()
                .block();

        if (tokens != null && !tokens.isEmpty()) {
            for (TokenEntity entity : tokens) {
                entity.setRevoked(true);
                cassandraRepository.save(entity).block();
            }
            return tokens.size();
        }
        return 0;
    }

    @Override
    public int revokeAllRefreshTokensByUserId(UUID userId) {
        var tokens = cassandraRepository.findByUserIdAndTokenType(userId, TokenType.REFRESH.name())
                .filter(entity -> !entity.isRevoked())
                .collectList()
                .block();

        if (tokens != null && !tokens.isEmpty()) {
            for (TokenEntity entity : tokens) {
                entity.setRevoked(true);
                cassandraRepository.save(entity).block();
            }
            return tokens.size();
        }
        return 0;
    }
}
