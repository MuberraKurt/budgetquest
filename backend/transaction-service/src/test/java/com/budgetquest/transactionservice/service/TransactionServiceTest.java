package com.budgetquest.transactionservice.service;

import com.budgetquest.transactionservice.dto.TransactionRequest;
import com.budgetquest.transactionservice.entity.Transaction;
import com.budgetquest.transactionservice.entity.TransactionType;
import com.budgetquest.transactionservice.event.TransactionChangedEvent;
import com.budgetquest.transactionservice.event.TransactionEventPublisher;
import com.budgetquest.transactionservice.repository.TransactionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private TransactionEventPublisher eventPublisher;

    @InjectMocks
    private TransactionService service;

    @Test
    void createPublishesCreatedEvent() {
        UUID transactionId = UUID.randomUUID();
        String userId = "user-123";

        TransactionRequest request = new TransactionRequest(
                TransactionType.EXPENSE,
                BigDecimal.valueOf(25.50),
                "Food",
                "Lunch",
                LocalDate.of(2026, 6, 13)
        );

        Transaction saved = transaction(
                transactionId,
                userId,
                TransactionType.EXPENSE,
                BigDecimal.valueOf(25.50),
                "Food",
                "Lunch",
                LocalDate.of(2026, 6, 13)
        );

        when(repository.save(any(Transaction.class))).thenReturn(saved);

        service.create(userId, request);

        ArgumentCaptor<TransactionChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(TransactionChangedEvent.class);

        verify(eventPublisher).publish(eventCaptor.capture());

        TransactionChangedEvent event = eventCaptor.getValue();

        assertThat(event.transactionId()).isEqualTo(transactionId);
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.type()).isEqualTo(TransactionType.EXPENSE);
        assertThat(event.amount()).isEqualByComparingTo("25.50");
        assertThat(event.category()).isEqualTo("Food");
        assertThat(event.transactionDate()).isEqualTo(LocalDate.of(2026, 6, 13));
        assertThat(event.action()).isEqualTo("CREATED");
        assertThat(event.previousType()).isNull();
        assertThat(event.previousAmount()).isNull();
        assertThat(event.previousTransactionDate()).isNull();
    }

    @Test
    void updatePublishesUpdatedEvent() {
        UUID transactionId = UUID.randomUUID();
        String userId = "user-123";

        Transaction existing = transaction(
                transactionId,
                userId,
                TransactionType.EXPENSE,
                BigDecimal.valueOf(10.00),
                "Old category",
                "Old description",
                LocalDate.of(2026, 6, 1)
        );

        TransactionRequest request = new TransactionRequest(
                TransactionType.INCOME,
                BigDecimal.valueOf(100.00),
                "Salary",
                "June salary",
                LocalDate.of(2026, 6, 13)
        );

        Transaction saved = transaction(
                transactionId,
                userId,
                TransactionType.INCOME,
                BigDecimal.valueOf(100.00),
                "Salary",
                "June salary",
                LocalDate.of(2026, 6, 13)
        );

        when(repository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(saved);

        service.update(userId, transactionId, request);

        ArgumentCaptor<TransactionChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(TransactionChangedEvent.class);

        verify(eventPublisher).publish(eventCaptor.capture());

        TransactionChangedEvent event = eventCaptor.getValue();

        assertThat(event.transactionId()).isEqualTo(transactionId);
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.type()).isEqualTo(TransactionType.INCOME);
        assertThat(event.amount()).isEqualByComparingTo("100.00");
        assertThat(event.category()).isEqualTo("Salary");
        assertThat(event.transactionDate()).isEqualTo(LocalDate.of(2026, 6, 13));
        assertThat(event.action()).isEqualTo("UPDATED");
        assertThat(event.previousType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(event.previousAmount()).isEqualByComparingTo("10.00");
        assertThat(event.previousTransactionDate()).isEqualTo(LocalDate.of(2026, 6, 1));
    }

    @Test
    void deletePublishesDeletedEvent() {
        UUID transactionId = UUID.randomUUID();
        String userId = "user-123";

        Transaction existing = transaction(
                transactionId,
                userId,
                TransactionType.EXPENSE,
                BigDecimal.valueOf(45.00),
                "Transport",
                "Bus card",
                LocalDate.of(2026, 6, 13)
        );

        when(repository.findByIdAndUserId(transactionId, userId)).thenReturn(Optional.of(existing));

        service.delete(userId, transactionId);

        verify(repository).delete(existing);

        ArgumentCaptor<TransactionChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(TransactionChangedEvent.class);

        verify(eventPublisher).publish(eventCaptor.capture());

        TransactionChangedEvent event = eventCaptor.getValue();

        assertThat(event.transactionId()).isEqualTo(transactionId);
        assertThat(event.userId()).isEqualTo(userId);
        assertThat(event.type()).isEqualTo(TransactionType.EXPENSE);
        assertThat(event.amount()).isEqualByComparingTo("45.00");
        assertThat(event.category()).isEqualTo("Transport");
        assertThat(event.transactionDate()).isEqualTo(LocalDate.of(2026, 6, 13));
        assertThat(event.action()).isEqualTo("DELETED");
        assertThat(event.previousType()).isNull();
        assertThat(event.previousAmount()).isNull();
        assertThat(event.previousTransactionDate()).isNull();
    }

    private Transaction transaction(
            UUID id,
            String userId,
            TransactionType type,
            BigDecimal amount,
            String category,
            String description,
            LocalDate transactionDate
    ) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setUserId(userId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.setTransactionDate(transactionDate);
        return transaction;
    }
}
