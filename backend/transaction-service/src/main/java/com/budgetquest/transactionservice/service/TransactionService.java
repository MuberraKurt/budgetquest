package com.budgetquest.transactionservice.service;

import com.budgetquest.transactionservice.dto.TransactionRequest;
import com.budgetquest.transactionservice.dto.TransactionResponse;
import com.budgetquest.transactionservice.entity.Transaction;
import com.budgetquest.transactionservice.entity.TransactionType;
import com.budgetquest.transactionservice.event.TransactionEventPublisher;
import com.budgetquest.transactionservice.repository.TransactionRepository;
import com.budgetquest.transactionservice.event.TransactionChangedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository repository;
    private final TransactionEventPublisher eventPublisher;

    public TransactionService(
            TransactionRepository repository,
            TransactionEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> list(String userId, Integer month, Integer year, TransactionType type, String category) {
        validateMonthYear(month, year);

        LocalDate startDate = null;
        LocalDate endDate = null;
        if (month != null && year != null) {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();
        }

        String normalizedCategory = normalizeCategory(category);

        return repository.findFiltered(userId, startDate, endDate, type, normalizedCategory)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(String userId, UUID id) {
        Transaction transaction = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        return toResponse(transaction);
    }

    @Transactional
    public TransactionResponse create(String userId, TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        applyRequest(transaction, request);

        Transaction saved = repository.save(transaction);
        eventPublisher.publish(toEvent(saved, "CREATED"));

        return toResponse(saved);
    }

    @Transactional
    public TransactionResponse update(String userId, UUID id, TransactionRequest request) {
        Transaction transaction = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        TransactionType previousType = transaction.getType();
        BigDecimal previousAmount = transaction.getAmount();
        LocalDate previousTransactionDate = transaction.getTransactionDate();

        applyRequest(transaction, request);

        Transaction saved = repository.save(transaction);
        eventPublisher.publish(toUpdatedEvent(
                saved,
                previousType,
                previousAmount,
                previousTransactionDate
        ));

        return toResponse(saved);
    }

    @Transactional
    public void delete(String userId, UUID id) {
        Transaction transaction = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        repository.delete(transaction);
        eventPublisher.publish(toEvent(transaction, "DELETED"));
    }

    private void applyRequest(Transaction transaction, TransactionRequest request) {
        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setCategory(request.category().trim());
        transaction.setDescription(request.description());
        transaction.setTransactionDate(request.transactionDate());
    }

    private void validateMonthYear(Integer month, Integer year) {
        if ((month == null) != (year == null)) {
            throw new IllegalArgumentException("Month and year must be provided together");
        }

        if (month != null && (month < 1 || month > 12)) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        return category.trim().toLowerCase();
    }

    private TransactionChangedEvent toEvent(Transaction transaction, String action) {
        return new TransactionChangedEvent(
                transaction.getId(),
                transaction.getUserId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getTransactionDate(),
                action,
                null,
                null,
                null
        );
    }

    private TransactionChangedEvent toUpdatedEvent(
            Transaction saved,
            TransactionType previousType,
            BigDecimal previousAmount,
            LocalDate previousTransactionDate
    ) {
        return new TransactionChangedEvent(
                saved.getId(),
                saved.getUserId(),
                saved.getType(),
                saved.getAmount(),
                saved.getCategory(),
                saved.getTransactionDate(),
                "UPDATED",
                previousType,
                previousAmount,
                previousTransactionDate
        );
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
