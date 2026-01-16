package com.lifeinventory.identity.infrastructure.persistence.cassandra.repository;

import com.lifeinventory.identity.infrastructure.persistence.cassandra.entity.TokenEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CassandraTokenRepository extends ReactiveCassandraRepository<TokenEntity, UUID> {

    @AllowFiltering
    Mono<TokenEntity> findByTokenValue(String tokenValue);

    @AllowFiltering
    Mono<TokenEntity> findByTokenValueAndTokenType(String tokenValue, String tokenType);

    @AllowFiltering
    Flux<TokenEntity> findByUserId(UUID userId);

    @AllowFiltering
    Flux<TokenEntity> findByUserIdAndTokenType(UUID userId, String tokenType);

    @AllowFiltering
    Mono<Void> deleteByTokenValue(String tokenValue);

    @AllowFiltering
    Mono<Void> deleteAllByUserId(UUID userId);

    @AllowFiltering
    Mono<Void> deleteAllByUserIdAndTokenType(UUID userId, String tokenType);
}
