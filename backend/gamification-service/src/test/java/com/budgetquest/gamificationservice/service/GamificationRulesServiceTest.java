package com.budgetquest.gamificationservice.service;

import com.budgetquest.gamificationservice.event.TransactionChangedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GamificationRulesServiceTest {

    private final GamificationRulesService rules = new GamificationRulesService();

    @Test
    void xpRulesAreDeterministicByTransactionType() {
        assertThat(rules.xpForType("INCOME")).isEqualTo(20);
        assertThat(rules.xpForType("EXPENSE")).isEqualTo(10);
    }

    @Test
    void levelIncreasesEveryHundredXp() {
        assertThat(rules.levelFromXp(0)).isEqualTo(1);
        assertThat(rules.levelFromXp(99)).isEqualTo(1);
        assertThat(rules.levelFromXp(100)).isEqualTo(2);
        assertThat(rules.levelFromXp(250)).isEqualTo(3);
    }

    @Test
    void streakRulesHandleFirstSameNextAndBrokenDays() {
        LocalDate juneTen = LocalDate.of(2026, 6, 10);

        assertThat(rules.updateStreak(0, null, juneTen)).isEqualTo(1);
        assertThat(rules.updateStreak(3, juneTen, juneTen)).isEqualTo(3);
        assertThat(rules.updateStreak(3, juneTen, juneTen.plusDays(1))).isEqualTo(4);
        assertThat(rules.updateStreak(3, juneTen, juneTen.plusDays(2))).isEqualTo(1);
    }

    @Test
    void createdEventAwardsExpectedXp() {
        TransactionChangedEvent event = new TransactionChangedEvent(
                UUID.randomUUID(),
                "user-123",
                "EXPENSE",
                BigDecimal.valueOf(25),
                "Food",
                LocalDate.of(2026, 6, 21),
                "CREATED",
                null,
                null,
                null
        );

        assertThat(rules.xpForEvent(event)).isEqualTo(10);
    }
}
