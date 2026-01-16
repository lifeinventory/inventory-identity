package com.lifeinventory.identity.api.controller;

import com.lifeinventory.identity.api.dto.*;
import com.lifeinventory.identity.infrastructure.security.GoogleAuthService;
import com.lifeinventory.identity.model.*;
import com.lifeinventory.identity.repository.TokenRepository;
import com.lifeinventory.identity.repository.UserRepository;
import com.lifeinventory.identity.service.TokenGenerator;
import com.lifeinventory.identity.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUserUseCase logoutUserUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;
    private final GoogleAuthService googleAuthService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final TokenGenerator tokenGenerator;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Mono.fromCallable(() -> {
            UserProfile profile = request.displayName() != null
                    ? UserProfile.ofDisplayName(request.displayName())
                    : UserProfile.empty();

            RegisterUserUseCase.RegisterCommand command = RegisterUserUseCase.RegisterCommand.local(
                    request.email(),
                    request.password(),
                    profile
            );

            User user = registerUserUseCase.execute(command);

            // For local registration, generate tokens immediately (email verification is separate)
            Token accessToken = tokenGenerator.generateAccessToken(user);
            Token refreshToken = tokenGenerator.generateRefreshToken(user);
            tokenRepository.save(accessToken);
            tokenRepository.save(refreshToken);

            return new AuthResponse(
                    accessToken.tokenValue(),
                    refreshToken.tokenValue(),
                    UserResponse.from(user)
            );
        });
    }

    @PostMapping("/login")
    public Mono<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return Mono.fromCallable(() -> {
            AuthenticateUserUseCase.AuthenticateCommand command =
                    AuthenticateUserUseCase.AuthenticateCommand.local(request.email(), request.password());

            AuthenticationResult result = authenticateUserUseCase.execute(command);

            return new AuthResponse(
                    result.accessToken().tokenValue(),
                    result.refreshToken().tokenValue(),
                    UserResponse.from(result.user())
            );
        });
    }

    @PostMapping("/google")
    public Mono<AuthResponse> authenticateWithGoogle(@Valid @RequestBody GoogleAuthRequest request) {
        return googleAuthService.verifyIdToken(request.idToken())
                .map(googleUser -> {
                    // Find or create user
                    User user = userRepository.findByProviderAndExternalId(AuthProvider.GOOGLE, googleUser.googleId())
                            .orElseGet(() -> {
                                // Check if email exists with different provider
                                var existingUser = userRepository.findByEmail(googleUser.email());
                                if (existingUser.isPresent()) {
                                    throw new IllegalStateException(
                                            "Email already registered with different authentication method");
                                }

                                // Create new user
                                UserProfile profile = new UserProfile(
                                        googleUser.name(),
                                        null,
                                        null,
                                        googleUser.pictureUrl(),
                                        "en",
                                        "UTC"
                                );

                                User newUser = User.createExternal(
                                        googleUser.email(),
                                        AuthProvider.GOOGLE,
                                        googleUser.googleId(),
                                        profile
                                );

                                return userRepository.save(newUser);
                            });

                    // Record login and generate tokens
                    User updatedUser = user.recordLogin();
                    userRepository.save(updatedUser);

                    Token accessToken = tokenGenerator.generateAccessToken(updatedUser);
                    Token refreshToken = tokenGenerator.generateRefreshToken(updatedUser);
                    tokenRepository.save(accessToken);
                    tokenRepository.save(refreshToken);

                    return new AuthResponse(
                            accessToken.tokenValue(),
                            refreshToken.tokenValue(),
                            UserResponse.from(updatedUser)
                    );
                });
    }

    @PostMapping("/refresh")
    public Mono<AuthResponse> refreshTokens(@Valid @RequestBody RefreshRequest request) {
        return Mono.fromCallable(() -> {
            RefreshTokenUseCase.RefreshCommand command =
                    RefreshTokenUseCase.RefreshCommand.of(request.refreshToken());

            AuthenticationResult result = refreshTokenUseCase.execute(command);

            return new AuthResponse(
                    result.accessToken().tokenValue(),
                    result.refreshToken().tokenValue(),
                    UserResponse.from(result.user())
            );
        });
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> logout(
            @Valid @RequestBody RefreshRequest request,
            @AuthenticationPrincipal User user
    ) {
        return Mono.fromRunnable(() -> {
            if (user != null) {
                LogoutUserUseCase.LogoutCommand command =
                        LogoutUserUseCase.LogoutCommand.single(user.id(), request.refreshToken());
                logoutUserUseCase.execute(command);
            }
        });
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return Mono.fromRunnable(() -> {
            RequestPasswordResetUseCase.RequestPasswordResetCommand command =
                    RequestPasswordResetUseCase.RequestPasswordResetCommand.of(request.email());
            // Always return success to prevent email enumeration
            requestPasswordResetUseCase.execute(command);
        });
    }

    @PostMapping("/reset-password")
    public Mono<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return Mono.fromRunnable(() -> {
            ResetPasswordUseCase.ResetPasswordCommand command =
                    new ResetPasswordUseCase.ResetPasswordCommand(request.token(), request.newPassword());
            resetPasswordUseCase.execute(command);
        });
    }

    @GetMapping("/me")
    public Mono<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        return Mono.justOrEmpty(user)
                .map(UserResponse::from)
                .switchIfEmpty(Mono.error(new IllegalStateException("User not authenticated")));
    }
}
