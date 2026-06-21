package com.budgetquest.gamificationservice.dto;

public record StreakResponse(
        String userId,
        int currentStreak
) {
}
