package com.budgetquest.transactionservice.repository;

import com.budgetquest.transactionservice.entity.Transaction;
import com.budgetquest.transactionservice.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUserIdOrderByTransactionDateDescCreatedAtDesc(String userId);
    Optional<Transaction> findByIdAndUserId(UUID id, String userId);

    @Query("""
            select t
            from Transaction t
            where t.userId = :userId
              and t.transactionDate >= coalesce(:startDate, t.transactionDate)
              and t.transactionDate <= coalesce(:endDate, t.transactionDate)
              and t.type = coalesce(:type, t.type)
              and lower(t.category) = coalesce(:category, lower(t.category))
            order by t.transactionDate desc, t.createdAt desc
            """)
    List<Transaction> findFiltered(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type,
            @Param("category") String category
    );
}
