package com.budgetquest.budgetservice.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyBudgetSummaryResponse(
        String userId,
        int year,
        int month,
        BigDecimal incomeTotal,
        BigDecimal expenseTotal,
        BigDecimal balance,
        BigDecimal netSavings,
        BigDecimal savingsRate,
        BigDecimal currentBalance,
        List<CategoryBreakdownResponse> categoryBreakdown
) {
}
