package com.budgetquest.budgetservice.dto;

import java.math.BigDecimal;
import java.util.List;

public record YearlyBudgetSummaryResponse(
        String userId,
        int year,
        BigDecimal incomeTotal,
        BigDecimal expenseTotal,
        BigDecimal netSavings,
        BigDecimal savingsRate,
        BigDecimal currentBalance,
        List<MonthlyBudgetSummaryResponse> months
) {
}
