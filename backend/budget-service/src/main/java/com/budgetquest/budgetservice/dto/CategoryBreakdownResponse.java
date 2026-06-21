package com.budgetquest.budgetservice.dto;

import java.math.BigDecimal;

public record CategoryBreakdownResponse(
        String category,
        BigDecimal amount
) {
}
