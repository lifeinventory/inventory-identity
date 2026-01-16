-- Identity Service Cassandra Schema

-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS identity
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE identity;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email TEXT,
    password_hash TEXT,
    auth_provider TEXT,
    external_id TEXT,
    display_name TEXT,
    first_name TEXT,
    last_name TEXT,
    avatar_url TEXT,
    locale TEXT,
    timezone TEXT,
    roles SET<TEXT>,
    permissions SET<TEXT>,
    email_verified BOOLEAN,
    active BOOLEAN,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Index for email lookup
CREATE INDEX IF NOT EXISTS users_email_idx ON users (email);

-- Index for external provider lookup
CREATE INDEX IF NOT EXISTS users_provider_external_id_idx ON users (auth_provider);
CREATE INDEX IF NOT EXISTS users_external_id_idx ON users (external_id);

-- Tokens table
CREATE TABLE IF NOT EXISTS tokens (
    id UUID PRIMARY KEY,
    user_id UUID,
    token_type TEXT,
    token_value TEXT,
    expires_at TIMESTAMP,
    created_at TIMESTAMP,
    revoked BOOLEAN
);

-- Index for token lookup by value
CREATE INDEX IF NOT EXISTS tokens_value_idx ON tokens (token_value);

-- Index for user's tokens lookup
CREATE INDEX IF NOT EXISTS tokens_user_id_idx ON tokens (user_id);

-- Tokens by user table (for efficient user token queries)
CREATE TABLE IF NOT EXISTS tokens_by_user (
    user_id UUID,
    token_type TEXT,
    token_id UUID,
    token_value TEXT,
    expires_at TIMESTAMP,
    created_at TIMESTAMP,
    revoked BOOLEAN,
    PRIMARY KEY ((user_id), token_type, created_at, token_id)
) WITH CLUSTERING ORDER BY (token_type ASC, created_at DESC, token_id ASC);
