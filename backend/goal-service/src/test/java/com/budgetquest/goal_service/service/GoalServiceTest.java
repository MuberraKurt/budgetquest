package com.budgetquest.goal_service.service;

import com.budgetquest.goal_service.dto.GoalRequest;
import com.budgetquest.goal_service.dto.GoalResponse;
import com.budgetquest.goal_service.entity.Goal;
import com.budgetquest.goal_service.entity.GoalPriority;
import com.budgetquest.goal_service.entity.GoalStatus;
import com.budgetquest.goal_service.repository.GoalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository repository;

    @InjectMocks
    private GoalService service;

    @Test
    void createMarksGoalCompletedWhenCurrentAmountReachesTargetAmount() {
        GoalRequest request = new GoalRequest(
                "Vacation",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000),
                null,
                GoalPriority.MEDIUM,
                "Travel",
                GoalStatus.ACTIVE
        );

        when(repository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalResponse response = service.create("user-123", request);

        assertThat(response.status()).isEqualTo(GoalStatus.COMPLETED);
        assertThat(response.progressPercent()).isEqualByComparingTo("100.00");
    }

    @Test
    void createKeepsRequestedStatusWhenCurrentAmountIsBelowTargetAmount() {
        GoalRequest request = new GoalRequest(
                "Emergency Fund",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(250),
                null,
                GoalPriority.HIGH,
                "Savings",
                GoalStatus.ACTIVE
        );

        when(repository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GoalResponse response = service.create("user-123", request);

        assertThat(response.status()).isEqualTo(GoalStatus.ACTIVE);
        assertThat(response.progressPercent()).isEqualByComparingTo("25.00");
    }
}
