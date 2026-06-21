package com.budgetquest.goal_service.dto;

import com.budgetquest.goal_service.entity.GoalPriority;
import com.budgetquest.goal_service.entity.GoalStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequest(
        @NotBlank
        @Size(max = 100)
        String title,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal targetAmount,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal currentAmount,

        LocalDate targetDate,

        @NotNull
        GoalPriority priority,

        @NotBlank
        @Size(max = 80)
        String category,

        @NotNull
        GoalStatus status
) {
}