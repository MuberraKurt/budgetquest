package com.budgetquest.transactionservice.event;

import com.budgetquest.transactionservice.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionChangedEvent(
        UUID transactionId,
        String userId,
        TransactionType type,
        BigDecimal amount,
        String category,
        LocalDate transactionDate,
        String action,
        TransactionType previousType,
        BigDecimal previousAmount,
        LocalDate previousTransactionDate
) {
}