package com.budgetquest.transactionservice.dto;

import com.budgetquest.transactionservice.entity.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull TransactionType type,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @Size(max = 80) String category,
        @Size(max = 255) String description,
        @NotNull LocalDate transactionDate
) {
}