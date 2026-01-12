# inventory-identity

Authentication & Authorization module for Life Inventory Platform.

## Overview

This module provides the core domain logic for user management, authentication, and authorization. It follows the same hexagonal architecture principles as `inventory-core`, keeping the domain logic clean and framework-agnostic.

## Module Structure

```
src/main/java/com/lifeinventory/identity/
├── model/                      # Domain models
│   ├── User.java               # Aggregate root - user entity
│   ├── UserProfile.java        # Value object - user profile info
│   ├── Token.java              # Value object - auth tokens
│   ├── Credentials.java        # Value object - login credentials
│   ├── AuthenticationResult.java # Value object - auth result with tokens
│   ├── Role.java               # Enum - user roles (USER, PREMIUM, ADMIN)
│   ├── Permission.java         # Enum - fine-grained permissions
│   ├── AuthProvider.java       # Enum - auth providers (LOCAL, GOOGLE, etc.)
│   └── TokenType.java          # Enum - token types (ACCESS, REFRESH, etc.)
│
├── usecase/                    # Use case interfaces (input ports)
│   ├── RegisterUserUseCase.java
│   ├── AuthenticateUserUseCase.java
│   ├── RefreshTokenUseCase.java
│   ├── LogoutUserUseCase.java
│   ├── GetUserUseCase.java
│   ├── UpdateUserProfileUseCase.java
│   ├── ChangePasswordUseCase.java
│   ├── RequestPasswordResetUseCase.java
│   ├── ResetPasswordUseCase.java
│   └── VerifyEmailUseCase.java
│
├── repository/                 # Repository interfaces (output ports)
│   ├── UserRepository.java
│   └── TokenRepository.java
│
├── service/                    # Domain services
│   ├── UserService.java        # Implements user-related use cases
│   ├── AuthenticationService.java # Implements auth-related use cases
│   ├── PasswordHasher.java     # Output port for password hashing
│   └── TokenGenerator.java     # Output port for token generation
│
├── event/                      # Domain events
│   ├── IdentityEvent.java      # Sealed interface for all identity events
│   ├── IdentityEventPublisher.java # Output port for event publishing
│   ├── UserRegistered.java
│   ├── UserAuthenticated.java
│   ├── UserLoggedOut.java
│   ├── PasswordResetRequested.java
│   ├── PasswordChanged.java
│   ├── EmailVerificationRequested.java
│   ├── EmailVerified.java
│   ├── UserProfileUpdated.java
│   └── TokenRefreshed.java
│
└── exception/                  # Domain exceptions
    ├── UserNotFoundException.java
    ├── UserAlreadyExistsException.java
    ├── InvalidCredentialsException.java
    ├── UserNotActiveException.java
    ├── EmailNotVerifiedException.java
    ├── InvalidTokenException.java
    ├── TokenExpiredException.java
    └── UnauthorizedAccessException.java
```

## Key Concepts

### User Model

The `User` record is the aggregate root for identity management:

```java
User user = User.createLocal("user@example.com", passwordHash);
User externalUser = User.createExternal(
    "user@example.com",
    AuthProvider.GOOGLE,
    "google-id-123",
    UserProfile.ofName("John", "Doe")
);
```

### Roles and Permissions

- **USER** - Basic access to own inventory
- **PREMIUM** - Extended features (export/import)
- **ADMIN** - Full platform management

Permissions are automatically assigned based on roles.

### Authentication Flow

1. **Local Auth**: Email/password with email verification
2. **OAuth**: Google, Apple, Facebook (email pre-verified)

### Tokens

- **ACCESS** - Short-lived (1 hour) for API requests
- **REFRESH** - Long-lived (30 days) for token refresh
- **PASSWORD_RESET** - For password recovery flow
- **EMAIL_VERIFICATION** - For email confirmation

## Dependencies

This module has minimal dependencies:
- Java 21
- Lombok (compile-only)
- JUnit 5 (test)

No framework dependencies - all Spring/infrastructure code belongs in `inventory-infrastructure`.

## Output Ports (SPIs)

These interfaces must be implemented by the infrastructure layer:

- `UserRepository` - User persistence
- `TokenRepository` - Token persistence
- `PasswordHasher` - Password hashing (e.g., BCrypt)
- `TokenGenerator` - Token generation (e.g., JWT)
- `IdentityEventPublisher` - Event publishing (e.g., Kafka)

## Usage

```java
// Wire up dependencies (done by DI framework in real app)
UserService userService = new UserService(
    userRepository,
    tokenRepository,
    passwordHasher,
    tokenGenerator,
    eventPublisher
);

// Register a user
User user = userService.execute(
    RegisterUserUseCase.RegisterCommand.local("user@example.com", "password123")
);

// Authenticate
AuthenticationResult result = authService.execute(
    AuthenticateUserUseCase.AuthenticateCommand.local("user@example.com", "password123")
);
```

## Building

```bash
./gradlew build
./gradlew test
```

## Related Modules

- `inventory-core` - Core inventory domain
- `inventory-infrastructure` - Database and external service adapters
- `inventory-api` - REST API layer
