package com.lifeinventory.identity.service;

import com.lifeinventory.identity.event.*;
import com.lifeinventory.identity.exception.*;
import com.lifeinventory.identity.model.*;
import com.lifeinventory.identity.repository.TokenRepository;
import com.lifeinventory.identity.repository.UserRepository;
import com.lifeinventory.identity.usecase.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain service implementing user-related use cases.
 */
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService implements
    RegisterUserUseCase,
    GetUserUseCase,
    UpdateUserProfileUseCase,
    ChangePasswordUseCase,
    VerifyEmailUseCase {

    @NonNull UserRepository userRepository;
    @NonNull TokenRepository tokenRepository;
    @NonNull PasswordHasher passwordHasher;
    @NonNull TokenGenerator tokenGenerator;
    @NonNull IdentityEventPublisher eventPublisher;

    @Override
    public User execute(RegisterCommand command) {
        String normalizedEmail = command.email().toLowerCase().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException(normalizedEmail);
        }

        User user;
        if (command.isLocalRegistration()) {
            String passwordHash = passwordHasher.hash(command.password());
            user = User.createLocal(normalizedEmail, passwordHash);
            if (command.profile() != null && !command.profile().equals(UserProfile.empty())) {
                user = user.withProfile(command.profile());
            }
        } else {
            user = User.createExternal(
                normalizedEmail,
                command.provider(),
                command.externalId(),
                command.profile()
            );
        }

        User saved = userRepository.save(user);
        eventPublisher.publish(UserRegistered.of(saved));

        // Generate email verification token for local registration
        if (command.isLocalRegistration()) {
            Token verificationToken = tokenGenerator.generateEmailVerificationToken(saved);
            tokenRepository.save(verificationToken);
            eventPublisher.publish(EmailVerificationRequested.of(saved, verificationToken));
        }

        return saved;
    }

    @Override
    public Optional<User> getById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    @Override
    public Optional<User> getByExternalId(String provider, String externalId) {
        AuthProvider authProvider;
        try {
            authProvider = AuthProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        return userRepository.findByProviderAndExternalId(authProvider, externalId);
    }

    @Override
    public List<User> getAll(int page, int size) {
        return userRepository.findAll(page, size);
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public User execute(UpdateProfileCommand command) {
        User user = userRepository.findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException(command.userId()));

        // Check authorization
        if (!command.requesterId().equals(command.userId())) {
            User requester = userRepository.findById(command.requesterId())
                .orElseThrow(() -> new UserNotFoundException(command.requesterId()));

            if (!requester.hasPermission(Permission.USER_UPDATE_ANY)) {
                throw UnauthorizedAccessException.forUser(command.requesterId(), command.userId());
            }
        }

        UserProfile updatedProfile = command.applyTo(user.profile());
        User updatedUser = user.withProfile(updatedProfile);
        User saved = userRepository.save(updatedUser);

        eventPublisher.publish(UserProfileUpdated.of(saved));

        return saved;
    }

    @Override
    public User execute(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException(command.userId()));

        if (!user.isLocalAuth()) {
            throw new IllegalStateException("Cannot change password for external auth provider");
        }

        if (!passwordHasher.verify(command.currentPassword(), user.passwordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        String newPasswordHash = passwordHasher.hash(command.newPassword());
        User updatedUser = user.withPasswordHash(newPasswordHash);
        User saved = userRepository.save(updatedUser);

        // Revoke all refresh tokens to force re-login on other devices
        tokenRepository.revokeAllRefreshTokensByUserId(user.id());

        eventPublisher.publish(PasswordChanged.of(saved));

        return saved;
    }

    @Override
    public User execute(VerifyEmailCommand command) {
        Token token = tokenRepository.findByTokenValueAndType(command.token(), TokenType.EMAIL_VERIFICATION)
            .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        if (token.isExpired()) {
            throw new TokenExpiredException("Verification token has expired");
        }

        if (token.revoked()) {
            throw new InvalidTokenException("Verification token has been revoked");
        }

        User user = userRepository.findById(token.userId())
            .orElseThrow(() -> new UserNotFoundException(token.userId()));

        if (user.emailVerified()) {
            // Already verified, just return the user
            return user;
        }

        User verifiedUser = user.markEmailVerified();
        User saved = userRepository.save(verifiedUser);

        // Revoke the verification token
        tokenRepository.save(token.revoke());

        eventPublisher.publish(EmailVerified.of(saved));

        return saved;
    }
}
