package com.budgetquest.transactionservice.dto;

import com.budgetquest.transactionservice.entity.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        String category,
        String description,
        LocalDate transactionDate,
        Instant createdAt,
        Instant updatedAt
) {
}