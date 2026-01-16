package com.lifeinventory.identity.infrastructure.persistence.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("tokens")
public class TokenEntity {

    @PrimaryKey
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column("token_type")
    private String tokenType;

    @Column("token_value")
    private String tokenValue;

    @Column("expires_at")
    private Instant expiresAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("revoked")
    private boolean revoked;
}
