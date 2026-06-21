package com.budgetquest.userservice.dto;

import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String keycloakUserId,
        String displayName,
        String currency,
        Instant createdAt,
        Instant updatedAt
) {
}