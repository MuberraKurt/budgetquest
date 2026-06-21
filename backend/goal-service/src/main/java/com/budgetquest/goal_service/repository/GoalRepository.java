package com.budgetquest.goal_service.repository;

import com.budgetquest.goal_service.entity.Goal;
import com.budgetquest.goal_service.entity.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Goal> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, GoalStatus status);

    Optional<Goal> findByIdAndUserId(UUID id, String userId);
}