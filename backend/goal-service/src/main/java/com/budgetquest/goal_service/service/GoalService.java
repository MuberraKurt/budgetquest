package com.budgetquest.goal_service.service;

import com.budgetquest.goal_service.dto.GoalRequest;
import com.budgetquest.goal_service.dto.GoalResponse;
import com.budgetquest.goal_service.entity.Goal;
import com.budgetquest.goal_service.entity.GoalStatus;
import com.budgetquest.goal_service.repository.GoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class GoalService {
    private final GoalRepository repository;

    public GoalService(GoalRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> list(String userId, GoalStatus status) {
        List<Goal> goals = status == null
                ? repository.findByUserIdOrderByCreatedAtDesc(userId)
                : repository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);

        return goals.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GoalResponse get(String userId, UUID id) {
        Goal goal = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        return toResponse(goal);
    }

    @Transactional
    public GoalResponse create(String userId, GoalRequest request) {
        Goal goal = new Goal();
        goal.setUserId(userId);
        applyRequest(goal, request);

        return toResponse(repository.save(goal));
    }

    @Transactional
    public GoalResponse update(String userId, UUID id, GoalRequest request) {
        Goal goal = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        applyRequest(goal, request);

        return toResponse(repository.save(goal));
    }

    @Transactional
    public void delete(String userId, UUID id) {
        Goal goal = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        repository.delete(goal);
    }

    private void applyRequest(Goal goal, GoalRequest request) {
        goal.setTitle(request.title().trim());
        goal.setTargetAmount(request.targetAmount());
        goal.setCurrentAmount(request.currentAmount());
        goal.setTargetDate(request.targetDate());
        goal.setPriority(request.priority());
        goal.setCategory(request.category().trim());
        goal.setStatus(resolveStatus(request));
    }

    private GoalStatus resolveStatus(GoalRequest request) {
        if (request.currentAmount().compareTo(request.targetAmount()) >= 0) {
            return GoalStatus.COMPLETED;
        }

        return request.status();
    }

    private GoalResponse toResponse(Goal goal) {
        return new GoalResponse(
                goal.getId(),
                goal.getTitle(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getTargetDate(),
                goal.getPriority(),
                goal.getCategory(),
                goal.getStatus(),
                calculateProgressPercent(goal),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }

    private BigDecimal calculateProgressPercent(Goal goal) {
        if (goal.getTargetAmount() == null || goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return goal.getCurrentAmount()
                .multiply(BigDecimal.valueOf(100))
                .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);
    }
}
