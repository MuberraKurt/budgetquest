package com.budgetquest.budgetservice.service;

import com.budgetquest.budgetservice.dto.MonthlyBudgetSummaryResponse;
import com.budgetquest.budgetservice.dto.YearlyBudgetSummaryResponse;
import com.budgetquest.budgetservice.entity.MonthlyBudgetSummary;
import com.budgetquest.budgetservice.event.TransactionChangedEvent;
import com.budgetquest.budgetservice.repository.MonthlyBudgetSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BudgetSummaryService {

    private final MonthlyBudgetSummaryRepository repository;

    public BudgetSummaryService(MonthlyBudgetSummaryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void apply(TransactionChangedEvent event) {
        if ("CREATED".equals(event.action())) {
            applyCurrentEvent(event, BigDecimal.ONE);
            return;
        }

        if ("DELETED".equals(event.action())) {
            applyCurrentEvent(event, BigDecimal.valueOf(-1));
            return;
        }

        if ("UPDATED".equals(event.action())) {
            applyPreviousEvent(event, BigDecimal.valueOf(-1));
            applyCurrentEvent(event, BigDecimal.ONE);
        }
    }

    @Transactional(readOnly = true)
    public MonthlyBudgetSummaryResponse getMonthlySummary(String userId, int year, int month) {
        MonthlyBudgetSummary summary = repository
                .findByUserIdAndYearAndMonth(userId, year, month)
                .orElseGet(() -> emptySummary(userId, year, month));

        return toResponse(summary);
    }

    @Transactional(readOnly = true)
    public YearlyBudgetSummaryResponse getYearlySummary(String userId, int year) {
        List<MonthlyBudgetSummaryResponse> months = repository
                .findByUserIdAndYearOrderByMonthAsc(userId, year)
                .stream()
                .map(this::toResponse)
                .toList();

        BigDecimal incomeTotal = months.stream()
                .map(MonthlyBudgetSummaryResponse::incomeTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenseTotal = months.stream()
                .map(MonthlyBudgetSummaryResponse::expenseTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netSavings = incomeTotal.subtract(expenseTotal);
        BigDecimal savingsRate = calculateSavingsRate(incomeTotal, netSavings);

        return new YearlyBudgetSummaryResponse(
                userId,
                year,
                incomeTotal,
                expenseTotal,
                netSavings,
                savingsRate,
                netSavings,
                months
        );
    }

    private MonthlyBudgetSummary emptySummary(String userId, int year, int month) {
        MonthlyBudgetSummary summary = new MonthlyBudgetSummary();
        summary.setUserId(userId);
        summary.setYear(year);
        summary.setMonth(month);
        summary.setIncomeTotal(BigDecimal.ZERO);
        summary.setExpenseTotal(BigDecimal.ZERO);
        return summary;
    }

    private MonthlyBudgetSummaryResponse toResponse(MonthlyBudgetSummary summary) {
        BigDecimal balance = summary.getIncomeTotal().subtract(summary.getExpenseTotal());
        BigDecimal savingsRate = calculateSavingsRate(summary.getIncomeTotal(), balance);

        return new MonthlyBudgetSummaryResponse(
                summary.getUserId(),
                summary.getYear(),
                summary.getMonth(),
                summary.getIncomeTotal(),
                summary.getExpenseTotal(),
                balance,
                balance,
                savingsRate,
                balance,
                List.of()
        );
    }

    private BigDecimal calculateSavingsRate(BigDecimal incomeTotal, BigDecimal netSavings) {
        if (incomeTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return netSavings
                .multiply(BigDecimal.valueOf(100))
                .divide(incomeTotal, 2, RoundingMode.HALF_UP);
    }

    private void applyCurrentEvent(TransactionChangedEvent event, BigDecimal direction) {
        MonthlyBudgetSummary summary = repository
                .findByUserIdAndYearAndMonth(
                        event.userId(),
                        event.transactionDate().getYear(),
                        event.transactionDate().getMonthValue()
                )
                .orElseGet(() -> newSummary(event.userId(), event.transactionDate().getYear(), event.transactionDate().getMonthValue()));

        applyAmount(summary, event.type(), event.amount(), direction);
        repository.save(summary);
    }

    private void applyPreviousEvent(TransactionChangedEvent event, BigDecimal direction) {
        if (event.previousType() == null
                || event.previousAmount() == null
                || event.previousTransactionDate() == null) {
            return;
        }

        MonthlyBudgetSummary summary = repository
                .findByUserIdAndYearAndMonth(
                        event.userId(),
                        event.previousTransactionDate().getYear(),
                        event.previousTransactionDate().getMonthValue()
                )
                .orElseGet(() -> newSummary(
                        event.userId(),
                        event.previousTransactionDate().getYear(),
                        event.previousTransactionDate().getMonthValue()
                ));

        applyAmount(summary, event.previousType(), event.previousAmount(), direction);
        repository.save(summary);
    }

    private void applyAmount(
            MonthlyBudgetSummary summary,
            String type,
            BigDecimal amount,
            BigDecimal direction
    ) {
        BigDecimal signedAmount = amount.multiply(direction);

        if ("INCOME".equals(type)) {
            summary.setIncomeTotal(summary.getIncomeTotal().add(signedAmount));
        } else if ("EXPENSE".equals(type)) {
            summary.setExpenseTotal(summary.getExpenseTotal().add(signedAmount));
        }
    }

    private MonthlyBudgetSummary newSummary(String userId, int year, int month) {
        MonthlyBudgetSummary summary = new MonthlyBudgetSummary();
        summary.setUserId(userId);
        summary.setYear(year);
        summary.setMonth(month);
        summary.setIncomeTotal(BigDecimal.ZERO);
        summary.setExpenseTotal(BigDecimal.ZERO);
        return summary;
    }
}
