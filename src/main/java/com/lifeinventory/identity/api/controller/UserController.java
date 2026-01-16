package com.lifeinventory.identity.api.controller;

import com.lifeinventory.identity.api.dto.*;
import com.lifeinventory.identity.model.User;
import com.lifeinventory.identity.model.UserProfile;
import com.lifeinventory.identity.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;

    @GetMapping("/me")
    public Mono<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        return Mono.justOrEmpty(user)
                .map(UserResponse::from)
                .switchIfEmpty(Mono.error(new IllegalStateException("User not authenticated")));
    }

    @GetMapping("/{id}")
    public Mono<UserResponse> getUserById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User requester
    ) {
        return Mono.fromCallable(() -> {
            // Check if user is requesting their own data or has admin rights
            if (!requester.id().equals(id) && !requester.isAdmin()) {
                throw new IllegalStateException("Access denied");
            }

            return getUserUseCase.getById(id)
                    .map(UserResponse::from)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        });
    }

    @PutMapping("/{id}/profile")
    public Mono<UserResponse> updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal User requester
    ) {
        return Mono.fromCallable(() -> {
            UpdateUserProfileUseCase.UpdateProfileCommand command =
                    new UpdateUserProfileUseCase.UpdateProfileCommand(
                            id,
                            requester.id(),
                            request.displayName(),
                            request.firstName(),
                            request.lastName(),
                            request.avatarUrl(),
                            request.locale(),
                            request.timezone()
                    );

            User updated = updateUserProfileUseCase.execute(command);
            return UserResponse.from(updated);
        });
    }

    @PostMapping("/{id}/change-password")
    public Mono<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal User requester
    ) {
        return Mono.fromRunnable(() -> {
            if (!requester.id().equals(id)) {
                throw new IllegalStateException("Can only change your own password");
            }

            ChangePasswordUseCase.ChangePasswordCommand command =
                    new ChangePasswordUseCase.ChangePasswordCommand(
                            id,
                            request.currentPassword(),
                            request.newPassword()
                    );

            changePasswordUseCase.execute(command);
        });
    }

    @PostMapping("/verify-email")
    public Mono<UserResponse> verifyEmail(@RequestParam String token) {
        return Mono.fromCallable(() -> {
            VerifyEmailUseCase.VerifyEmailCommand command =
                    VerifyEmailUseCase.VerifyEmailCommand.of(token);

            User verified = verifyEmailUseCase.execute(command);
            return UserResponse.from(verified);
        });
    }
}
