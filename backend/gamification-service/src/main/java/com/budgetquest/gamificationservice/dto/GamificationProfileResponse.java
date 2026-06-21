package com.budgetquest.gamificationservice.dto;

public record GamificationProfileResponse(
        String userId,
        int xp,
        int level,
        int currentStreak,
        int totalTransactions
) {
}