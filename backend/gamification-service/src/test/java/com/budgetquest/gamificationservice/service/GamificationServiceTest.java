package com.budgetquest.gamificationservice.service;

import com.budgetquest.gamificationservice.entity.UserBadge;
import com.budgetquest.gamificationservice.entity.UserGamification;
import com.budgetquest.gamificationservice.event.TransactionChangedEvent;
import com.budgetquest.gamificationservice.repository.UserBadgeRepository;
import com.budgetquest.gamificationservice.repository.UserGamificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {

    @Mock
    private UserGamificationRepository gamificationRepository;

    @Mock
    private UserBadgeRepository badgeRepository;

    @Mock
    private GamificationRulesService rules;

    @InjectMocks
    private GamificationService service;

    @Test
    void createdTransactionUpdatesProfileAndAwardsFirstBadgeOnce() {
        TransactionChangedEvent event = event("CREATED", "EXPENSE", null);

        when(gamificationRepository.findById("user-123")).thenReturn(Optional.empty());
        when(rules.xpForEvent(event)).thenReturn(10);
        when(rules.levelFromXp(10)).thenReturn(1);
        when(rules.updateStreak(0, null, event.transactionDate())).thenReturn(1);
        when(rules.badgeForTransactionCount(1)).thenReturn(Optional.of("FIRST_TRANSACTION"));
        when(badgeRepository.findByUserIdAndBadgeCode("user-123", "FIRST_TRANSACTION"))
                .thenReturn(Optional.empty());
        when(gamificationRepository.save(any(UserGamification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.handleTransactionEvent(event);

        verify(gamificationRepository).save(org.mockito.ArgumentMatchers.argThat(profile ->
                profile.getUserId().equals("user-123")
                        && profile.getTotalTransactions() == 1
                        && profile.getXp() == 10
                        && profile.getCurrentStreak() == 1
        ));
        verify(badgeRepository).save(any(UserBadge.class));
    }

    @Test
    void duplicateBadgeIsNotAwardedAgain() {
        TransactionChangedEvent event = event("CREATED", "EXPENSE", null);

        when(gamificationRepository.findById("user-123")).thenReturn(Optional.empty());
        when(rules.xpForEvent(event)).thenReturn(10);
        when(rules.levelFromXp(10)).thenReturn(1);
        when(rules.updateStreak(0, null, event.transactionDate())).thenReturn(1);
        when(rules.badgeForTransactionCount(1)).thenReturn(Optional.of("FIRST_TRANSACTION"));
        when(badgeRepository.findByUserIdAndBadgeCode("user-123", "FIRST_TRANSACTION"))
                .thenReturn(Optional.of(new UserBadge()));

        service.handleTransactionEvent(event);

        verify(badgeRepository, never()).save(any(UserBadge.class));
    }

    @Test
    void updatedTransactionAdjustsXpWhenTypeChanges() {
        UserGamification profile = new UserGamification();
        profile.setUserId("user-123");
        profile.setXp(10);
        profile.setLevel(1);

        TransactionChangedEvent event = event("UPDATED", "INCOME", "EXPENSE");

        when(gamificationRepository.findById("user-123")).thenReturn(Optional.of(profile));
        when(rules.xpForType("EXPENSE")).thenReturn(10);
        when(rules.xpForType("INCOME")).thenReturn(20);
        when(rules.levelFromXp(20)).thenReturn(1);

        service.handleTransactionEvent(event);

        assertThat(profile.getXp()).isEqualTo(20);
        verify(gamificationRepository).save(profile);
    }

    @Test
    void deletedTransactionDecrementsTotalTransactionCountWithoutGoingNegative() {
        UserGamification profile = new UserGamification();
        profile.setUserId("user-123");
        profile.setTotalTransactions(0);

        when(gamificationRepository.findById("user-123")).thenReturn(Optional.of(profile));

        service.handleTransactionEvent(event("DELETED", "EXPENSE", null));

        assertThat(profile.getTotalTransactions()).isZero();
        verify(gamificationRepository).save(profile);
    }

    private TransactionChangedEvent event(String action, String type, String previousType) {
        return new TransactionChangedEvent(
                UUID.randomUUID(),
                "user-123",
                type,
                BigDecimal.valueOf(25),
                "Food",
                LocalDate.of(2026, 6, 21),
                action,
                previousType,
                previousType == null ? null : BigDecimal.valueOf(20),
                previousType == null ? null : LocalDate.of(2026, 6, 20)
        );
    }
}
