package com.budgetquest.gamificationservice.service;

import com.budgetquest.gamificationservice.event.TransactionChangedEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class GamificationRulesService {

    public int xpForEvent(TransactionChangedEvent event) {
        if (!"CREATED".equals(event.action())) {
            return 0;
        }
        return xpForType(event.type());
    }

    public int xpForType(String type) {
        return "INCOME".equals(type) ? 20 : 10;
    }

    public int levelFromXp(int xp) {
        return (xp / 100) + 1;
    }

    public int updateStreak(int currentStreak, LocalDate lastActive, LocalDate today) {
        if (lastActive == null) {
            return 1;
        }
        if (lastActive.equals(today)) {
            return currentStreak;
        }
        if (lastActive.plusDays(1).equals(today)) {
            return currentStreak + 1;
        }
        return 1;
    }

    public Optional<String> badgeForTransactionCount(int totalTransactions) {
        if (totalTransactions == 1) {
            return Optional.of("FIRST_TRANSACTION");
        }
        if (totalTransactions == 10) {
            return Optional.of("TEN_TRANSACTIONS");
        }
        return Optional.empty();
    }
}
