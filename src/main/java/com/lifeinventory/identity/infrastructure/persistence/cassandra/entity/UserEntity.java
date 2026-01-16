package com.lifeinventory.identity.infrastructure.persistence.cassandra.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class UserEntity {

    @PrimaryKey
    private UUID id;

    @Column("email")
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("auth_provider")
    private String authProvider;

    @Column("external_id")
    private String externalId;

    @Column("display_name")
    private String displayName;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("avatar_url")
    private String avatarUrl;

    @Column("locale")
    private String locale;

    @Column("timezone")
    private String timezone;

    @Column("roles")
    private Set<String> roles;

    @Column("permissions")
    private Set<String> permissions;

    @Column("email_verified")
    private boolean emailVerified;

    @Column("active")
    private boolean active;

    @Column("last_login_at")
    private Instant lastLoginAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;
}
