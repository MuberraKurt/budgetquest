package com.budgetquest.budgetservice.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionChangedEvent(
        UUID transactionId,
        String userId,
        String type,
        BigDecimal amount,
        String category,
        LocalDate transactionDate,
        String action,
        String previousType,
        BigDecimal previousAmount,
        LocalDate previousTransactionDate
) {
}