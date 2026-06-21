package com.budgetquest.gamificationservice.dto;

import java.time.Instant;

public record BadgeResponse(
        String badgeCode,
        Instant earnedAt
) {
}