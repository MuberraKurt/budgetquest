package com.budgetquest.goal_service.controller;

import com.budgetquest.goal_service.dto.GoalResponse;
import com.budgetquest.goal_service.entity.GoalPriority;
import com.budgetquest.goal_service.entity.GoalStatus;
import com.budgetquest.goal_service.service.GoalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalController.class)
class GoalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoalService goalService;

    @Test
    void listReturnsGoalsForAuthenticatedUser() throws Exception {
        String userId = "user-123";
        UUID goalId = UUID.randomUUID();

        GoalResponse response = new GoalResponse(
                goalId,
                "Emergency Fund",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(250),
                LocalDate.of(2026, 12, 31),
                GoalPriority.HIGH,
                "Savings",
                GoalStatus.ACTIVE,
                BigDecimal.valueOf(25),
                Instant.parse("2026-01-01T00:00:00Z"),
                Instant.parse("2026-01-01T00:00:00Z")
        );

        when(goalService.list(userId, null)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/goals").with(jwt().jwt(this::withSubject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(goalId.toString()))
                .andExpect(jsonPath("$[0].title").value("Emergency Fund"))
                .andExpect(jsonPath("$[0].progressPercent").value(25));

        verify(goalService).list(userId, null);
    }

    private void withSubject(Jwt.Builder builder) {
        builder.subject("user-123");
    }
}
