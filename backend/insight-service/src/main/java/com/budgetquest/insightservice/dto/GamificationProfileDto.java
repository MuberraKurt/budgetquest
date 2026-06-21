package com.budgetquest.insightservice.dto;

public record GamificationProfileDto(
        String userId,
        int xp,
        int level,
        int currentStreak,
        int totalTransactions
) {
}
