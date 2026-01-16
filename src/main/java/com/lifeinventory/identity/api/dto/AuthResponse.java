package com.lifeinventory.identity.api.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UserResponse user
) {}
