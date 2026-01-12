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

import java.util.Optional;

/**
 * Domain service implementing authentication-related use cases.
 */
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements
    AuthenticateUserUseCase,
    RefreshTokenUseCase,
    LogoutUserUseCase,
    RequestPasswordResetUseCase,
    ResetPasswordUseCase {

    @NonNull UserRepository userRepository;
    @NonNull TokenRepository tokenRepository;
    @NonNull PasswordHasher passwordHasher;
    @NonNull TokenGenerator tokenGenerator;
    @NonNull IdentityEventPublisher eventPublisher;

    @Override
    public AuthenticationResult execute(AuthenticateCommand command) {
        User user;

        if (command.isLocalAuthentication()) {
            user = authenticateLocal(command.email(), command.password());
        } else {
            // External authentication is handled by the infrastructure layer
            // This would typically validate the external token and get/create the user
            throw new UnsupportedOperationException(
                "External authentication must be handled by infrastructure adapter");
        }

        // Check if user can login
        if (!user.canLogin()) {
            if (!user.active()) {
                throw new UserNotActiveException(user.id());
            }
            if (!user.emailVerified()) {
                throw new EmailNotVerifiedException(user.id());
            }
        }

        // Generate tokens
        Token accessToken = tokenGenerator.generateAccessToken(user);
        Token refreshToken = tokenGenerator.generateRefreshToken(user);

        tokenRepository.save(accessToken);
        tokenRepository.save(refreshToken);

        // Update last login
        User updatedUser = user.recordLogin();
        userRepository.save(updatedUser);

        eventPublisher.publish(UserAuthenticated.of(updatedUser, command.ipAddress(), command.userAgent()));

        return new AuthenticationResult(updatedUser, accessToken, refreshToken);
    }

    private User authenticateLocal(String email, String password) {
        String normalizedEmail = email.toLowerCase().trim();

        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!user.isLocalAuth()) {
            throw new InvalidCredentialsException(
                "This account uses " + user.authProvider() + " authentication");
        }

        if (!passwordHasher.verify(password, user.passwordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return user;
    }

    @Override
    public AuthenticationResult execute(RefreshCommand command) {
        Token refreshToken = tokenRepository.findByTokenValueAndType(command.refreshToken(), TokenType.REFRESH)
            .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            throw new TokenExpiredException("Refresh token has expired");
        }

        if (refreshToken.revoked()) {
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        User user = userRepository.findById(refreshToken.userId())
            .orElseThrow(() -> new UserNotFoundException(refreshToken.userId()));

        if (!user.canLogin()) {
            throw new UserNotActiveException(user.id());
        }

        // Revoke the old refresh token
        tokenRepository.save(refreshToken.revoke());

        // Generate new tokens
        Token newAccessToken = tokenGenerator.generateAccessToken(user);
        Token newRefreshToken = tokenGenerator.generateRefreshToken(user);

        tokenRepository.save(newAccessToken);
        tokenRepository.save(newRefreshToken);

        eventPublisher.publish(TokenRefreshed.of(user));

        return new AuthenticationResult(user, newAccessToken, newRefreshToken);
    }

    @Override
    public void execute(LogoutCommand command) {
        if (command.logoutAllDevices()) {
            // Revoke all tokens for the user
            tokenRepository.revokeAllByUserId(command.userId());
            eventPublisher.publish(UserLoggedOut.allDevices(command.userId()));
        } else if (command.refreshToken() != null) {
            // Revoke only the specific refresh token
            tokenRepository.findByTokenValueAndType(command.refreshToken(), TokenType.REFRESH)
                .ifPresent(token -> tokenRepository.save(token.revoke()));
            eventPublisher.publish(UserLoggedOut.singleDevice(command.userId()));
        }
    }

    @Override
    public Optional<Token> execute(RequestPasswordResetCommand command) {
        String normalizedEmail = command.normalizedEmail();

        return userRepository.findByEmail(normalizedEmail)
            .filter(User::isLocalAuth)
            .map(user -> {
                // Revoke any existing password reset tokens
                tokenRepository.deleteAllByUserIdAndType(user.id(), TokenType.PASSWORD_RESET);

                // Generate new token
                Token resetToken = tokenGenerator.generatePasswordResetToken(user);
                Token saved = tokenRepository.save(resetToken);

                eventPublisher.publish(PasswordResetRequested.of(user, saved));

                return saved;
            });
    }

    @Override
    public User execute(ResetPasswordCommand command) {
        Token resetToken = tokenRepository.findByTokenValueAndType(command.token(), TokenType.PASSWORD_RESET)
            .orElseThrow(() -> new InvalidTokenException("Invalid password reset token"));

        if (resetToken.isExpired()) {
            throw new TokenExpiredException("Password reset token has expired");
        }

        if (resetToken.revoked()) {
            throw new InvalidTokenException("Password reset token has been revoked");
        }

        User user = userRepository.findById(resetToken.userId())
            .orElseThrow(() -> new UserNotFoundException(resetToken.userId()));

        // Hash new password and update user
        String newPasswordHash = passwordHasher.hash(command.newPassword());
        User updatedUser = user.withPasswordHash(newPasswordHash);
        User saved = userRepository.save(updatedUser);

        // Revoke the reset token
        tokenRepository.save(resetToken.revoke());

        // Revoke all refresh tokens to invalidate existing sessions
        tokenRepository.revokeAllRefreshTokensByUserId(user.id());

        eventPublisher.publish(PasswordChanged.of(saved));

        return saved;
    }
}
