package com.lifeinventory.identity.api.dto;

public record UpdateProfileRequest(
        String displayName,
        String firstName,
        String lastName,
        String avatarUrl,
        String locale,
        String timezone
) {}
