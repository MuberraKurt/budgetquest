package com.budgetquest.budgetservice.repository;

import com.budgetquest.budgetservice.entity.MonthlyBudgetSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonthlyBudgetSummaryRepository extends JpaRepository<MonthlyBudgetSummary, UUID> {

    Optional<MonthlyBudgetSummary> findByUserIdAndYearAndMonth(
            String userId,
            int year,
            int month
    );

    List<MonthlyBudgetSummary> findByUserIdAndYearOrderByMonthAsc(String userId, int year);
}