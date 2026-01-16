package com.lifeinventory.identity.infrastructure.persistence.cassandra.adapter;

import com.lifeinventory.identity.infrastructure.persistence.cassandra.mapper.EntityMapper;
import com.lifeinventory.identity.infrastructure.persistence.cassandra.repository.CassandraUserRepository;
import com.lifeinventory.identity.model.AuthProvider;
import com.lifeinventory.identity.model.User;
import com.lifeinventory.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final CassandraUserRepository cassandraRepository;
    private final EntityMapper mapper;

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        return cassandraRepository.save(entity)
                .map(mapper::toDomain)
                .block();
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return cassandraRepository.findById(userId)
                .map(mapper::toDomain)
                .blockOptional();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return cassandraRepository.findByEmail(email.toLowerCase().trim())
                .map(mapper::toDomain)
                .blockOptional();
    }

    @Override
    public Optional<User> findByProviderAndExternalId(AuthProvider provider, String externalId) {
        return cassandraRepository.findByAuthProviderAndExternalId(provider.name(), externalId)
                .map(mapper::toDomain)
                .blockOptional();
    }

    @Override
    public List<User> findAll(int page, int size) {
        return cassandraRepository.findAll()
                .skip((long) page * size)
                .take(size)
                .map(mapper::toDomain)
                .collectList()
                .block();
    }

    @Override
    public List<User> findAllActive(int page, int size) {
        return cassandraRepository.findAllByActive(true)
                .skip((long) page * size)
                .take(size)
                .map(mapper::toDomain)
                .collectList()
                .block();
    }

    @Override
    public void deleteById(UUID userId) {
        cassandraRepository.deleteById(userId).block();
    }

    @Override
    public boolean existsById(UUID userId) {
        return Boolean.TRUE.equals(cassandraRepository.existsById(userId).block());
    }

    @Override
    public boolean existsByEmail(String email) {
        return Boolean.TRUE.equals(cassandraRepository.existsByEmail(email.toLowerCase().trim()).block());
    }

    @Override
    public long count() {
        return Optional.ofNullable(cassandraRepository.count().block()).orElse(0L);
    }

    @Override
    public long countActive() {
        return Optional.ofNullable(cassandraRepository.countByActive(true).block()).orElse(0L);
    }
}
