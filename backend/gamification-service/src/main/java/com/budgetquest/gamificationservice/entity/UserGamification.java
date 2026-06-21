package com.budgetquest.gamificationservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_gamification")
public class UserGamification {

    @Id
    private String userId;

    @Column(nullable = false)
    private int xp = 0;

    @Column(nullable = false)
    private int level = 1;

    @Column(nullable = false)
    private int currentStreak = 0;

    private LocalDate lastActiveDate;

    @Column(nullable = false)
    private int totalTransactions = 0;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate lastActiveDate) { this.lastActiveDate = lastActiveDate; }

    public int getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(int totalTransactions) { this.totalTransactions = totalTransactions; }
}