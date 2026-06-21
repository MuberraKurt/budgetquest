package com.budgetquest.insightservice.dto;

import java.math.BigDecimal;

public record BudgetSummaryDto(
        String userId,
        int year,
        int month,
        BigDecimal incomeTotal,
        BigDecimal expenseTotal,
        BigDecimal balance
) {
}
