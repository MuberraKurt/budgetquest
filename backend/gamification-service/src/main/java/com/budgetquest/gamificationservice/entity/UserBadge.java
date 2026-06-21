package com.budgetquest.gamificationservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "user_badges",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "badgeCode"})
)
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String badgeCode;

    @Column(nullable = false)
    private Instant earnedAt;

    @PrePersist
    void prePersist() {
        earnedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getBadgeCode() { return badgeCode; }
    public void setBadgeCode(String badgeCode) { this.badgeCode = badgeCode; }
    public Instant getEarnedAt() { return earnedAt; }
}