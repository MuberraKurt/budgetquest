package com.budgetquest.gamificationservice.repository;

import com.budgetquest.gamificationservice.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {
    List<UserBadge> findByUserIdOrderByEarnedAtDesc(String userId);
    Optional<UserBadge> findByUserIdAndBadgeCode(String userId, String badgeCode);
}