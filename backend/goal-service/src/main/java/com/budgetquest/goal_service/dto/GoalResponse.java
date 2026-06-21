package com.budgetquest.goal_service.dto;

import com.budgetquest.goal_service.entity.GoalPriority;
import com.budgetquest.goal_service.entity.GoalStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GoalResponse(
        UUID id,
        String title,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        LocalDate targetDate,
        GoalPriority priority,
        String category,
        GoalStatus status,
        BigDecimal progressPercent,
        Instant createdAt,
        Instant updatedAt
) {
}