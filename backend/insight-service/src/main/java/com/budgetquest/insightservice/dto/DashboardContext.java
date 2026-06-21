package com.budgetquest.insightservice.dto;

import java.math.BigDecimal;

public record DashboardContext(
        BigDecimal monthlyBalance,
        int currentStreak,
        int totalTransactions,
        int level
) {
}
