package com.budgetquest.budgetservice.service;

import com.budgetquest.budgetservice.entity.MonthlyBudgetSummary;
import com.budgetquest.budgetservice.event.TransactionChangedEvent;
import com.budgetquest.budgetservice.repository.MonthlyBudgetSummaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetSummaryServiceTest {

    @Mock
    private MonthlyBudgetSummaryRepository repository;

    @InjectMocks
    private BudgetSummaryService service;

    @Test
    void createdIncomeAddsToMonthlyIncomeTotal() {
        TransactionChangedEvent event = new TransactionChangedEvent(
                UUID.randomUUID(),
                "user-123",
                "INCOME",
                BigDecimal.valueOf(1000),
                "Salary",
                LocalDate.of(2026, 6, 13),
                "CREATED",
                null,
                null,
                null
        );

        MonthlyBudgetSummary existing = new MonthlyBudgetSummary();
        existing.setUserId("user-123");
        existing.setYear(2026);
        existing.setMonth(6);
        existing.setIncomeTotal(BigDecimal.valueOf(200));
        existing.setExpenseTotal(BigDecimal.valueOf(50));

        when(repository.findByUserIdAndYearAndMonth("user-123", 2026, 6))
                .thenReturn(Optional.of(existing));

        service.apply(event);

        assertThat(existing.getIncomeTotal()).isEqualByComparingTo("1200");
        assertThat(existing.getExpenseTotal()).isEqualByComparingTo("50");
        verify(repository).save(existing);
    }

    @Test
    void createdExpenseAddsToMonthlyExpenseTotal() {
        TransactionChangedEvent event = new TransactionChangedEvent(
                UUID.randomUUID(),
                "user-123",
                "EXPENSE",
                BigDecimal.valueOf(25.50),
                "Food",
                LocalDate.of(2026, 6, 13),
                "CREATED",
                null,
                null,
                null
        );

        when(repository.findByUserIdAndYearAndMonth("user-123", 2026, 6))
                .thenReturn(Optional.empty());

        service.apply(event);

        ArgumentCaptor<MonthlyBudgetSummary> summaryCaptor =
                ArgumentCaptor.forClass(MonthlyBudgetSummary.class);

        verify(repository).save(summaryCaptor.capture());

        MonthlyBudgetSummary saved = summaryCaptor.getValue();

        assertThat(saved.getUserId()).isEqualTo("user-123");
        assertThat(saved.getYear()).isEqualTo(2026);
        assertThat(saved.getMonth()).isEqualTo(6);
        assertThat(saved.getIncomeTotal()).isEqualByComparingTo("0");
        assertThat(saved.getExpenseTotal()).isEqualByComparingTo("25.50");
    }

    @Test
    void updatedExpenseSubtractsPreviousAmountAndAddsCurrentAmount() {
        TransactionChangedEvent event = new TransactionChangedEvent(
                UUID.randomUUID(),
                "user-123",
                "EXPENSE",
                BigDecimal.valueOf(50),
                "Food",
                LocalDate.of(2026, 6, 13),
                "UPDATED",
                "EXPENSE",
                BigDecimal.valueOf(20),
                LocalDate.of(2026, 6, 13)
        );

        MonthlyBudgetSummary existing = new MonthlyBudgetSummary();
        existing.setUserId("user-123");
        existing.setYear(2026);
        existing.setMonth(6);
        existing.setIncomeTotal(BigDecimal.ZERO);
        existing.setExpenseTotal(BigDecimal.valueOf(100));

        when(repository.findByUserIdAndYearAndMonth("user-123", 2026, 6))
                .thenReturn(Optional.of(existing));

        service.apply(event);

        assertThat(existing.getExpenseTotal()).isEqualByComparingTo("130");
        verify(repository, org.mockito.Mockito.times(2)).save(existing);
    }

    @Test
    void updatedExpenseMovedToDifferentMonthUpdatesBothMonths() {
        TransactionChangedEvent event = new TransactionChangedEvent(
                UUID.randomUUID(),
                "user-123",
                "EXPENSE",
                BigDecimal.valueOf(50),
                "Food",
                LocalDate.of(2026, 7, 2),
                "UPDATED",
                "EXPENSE",
                BigDecimal.valueOf(20),
                LocalDate.of(2026, 6, 13)
        );

        MonthlyBudgetSummary june = new MonthlyBudgetSummary();
        june.setUserId("user-123");
        june.setYear(2026);
        june.setMonth(6);
        june.setIncomeTotal(BigDecimal.ZERO);
        june.setExpenseTotal(BigDecimal.valueOf(100));

        MonthlyBudgetSummary july = new MonthlyBudgetSummary();
        july.setUserId("user-123");
        july.setYear(2026);
        july.setMonth(7);
        july.setIncomeTotal(BigDecimal.ZERO);
        july.setExpenseTotal(BigDecimal.valueOf(10));

        when(repository.findByUserIdAndYearAndMonth("user-123", 2026, 6))
                .thenReturn(Optional.of(june));
        when(repository.findByUserIdAndYearAndMonth("user-123", 2026, 7))
                .thenReturn(Optional.of(july));

        service.apply(event);

        assertThat(june.getExpenseTotal()).isEqualByComparingTo("80");
        assertThat(july.getExpenseTotal()).isEqualByComparingTo("60");
        verify(repository).save(june);
        verify(repository).save(july);
    }

    @Test
    void unknownEventDoesNotChangeSummary() {
        TransactionChangedEvent event = new TransactionChangedEvent(
                UUID.randomUUID(),
                "user-123",
                "EXPENSE",
                BigDecimal.valueOf(25.50),
                "Food",
                LocalDate.of(2026, 6, 13),
                "IGNORED",
                null,
                null,
                null
        );

        service.apply(event);

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
