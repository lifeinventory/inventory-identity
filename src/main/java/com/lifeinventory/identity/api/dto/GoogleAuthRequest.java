package com.lifeinventory.identity.api.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(
        @NotBlank(message = "ID token is required")
        String idToken
) {}
