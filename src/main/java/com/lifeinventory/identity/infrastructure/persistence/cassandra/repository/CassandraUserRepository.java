package com.lifeinventory.identity.infrastructure.persistence.cassandra.repository;

import com.lifeinventory.identity.infrastructure.persistence.cassandra.entity.UserEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CassandraUserRepository extends ReactiveCassandraRepository<UserEntity, UUID> {

    @AllowFiltering
    Mono<UserEntity> findByEmail(String email);

    @AllowFiltering
    Mono<UserEntity> findByAuthProviderAndExternalId(String authProvider, String externalId);

    @AllowFiltering
    Flux<UserEntity> findAllByActive(boolean active);

    @AllowFiltering
    Mono<Boolean> existsByEmail(String email);

    @AllowFiltering
    Mono<Long> countByActive(boolean active);
}
