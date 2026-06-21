package com.budgetquest.insightservice.controller;

import com.budgetquest.insightservice.dto.CoachingMessageResponse;
import com.budgetquest.insightservice.dto.DashboardContext;
import com.budgetquest.insightservice.service.DashboardContextService;
import com.budgetquest.insightservice.service.InsightEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InsightController.class)
class InsightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardContextService contextService;

    @MockitoBean
    private InsightEngine insightEngine;

    @Test
    void coachingReturnsEducationalMessage() throws Exception {
        when(contextService.load("Bearer test-token")).thenReturn(
                new DashboardContext(BigDecimal.valueOf(100), 2, 3, 2)
        );
        when(insightEngine.generate(org.mockito.ArgumentMatchers.any())).thenReturn(
                new CoachingMessageResponse(
                        "GENERAL_TIP",
                        "You are level 2.",
                        InsightEngine.DISCLAIMER
                )
        );

        mockMvc.perform(get("/api/insights/coaching")
                        .with(jwt().jwt(this::withToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("GENERAL_TIP"))
                .andExpect(jsonPath("$.disclaimer").value(InsightEngine.DISCLAIMER));

        verify(contextService).load("Bearer test-token");
    }

    @Test
    void coachingRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/insights/coaching"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void dashboardInsightUsesSameEducationalEngine() throws Exception {
        when(contextService.load("Bearer test-token")).thenReturn(
                new DashboardContext(BigDecimal.valueOf(100), 2, 3, 2)
        );
        when(insightEngine.generate(org.mockito.ArgumentMatchers.any())).thenReturn(
                new CoachingMessageResponse(
                        "GENERAL_TIP",
                        "You are level 2.",
                        InsightEngine.DISCLAIMER
                )
        );

        mockMvc.perform(get("/api/insights/dashboard")
                        .with(jwt().jwt(this::withToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disclaimer").value(InsightEngine.DISCLAIMER));
    }

    @Test
    void coachMessagePostReturnsEducationalMessage() throws Exception {
        when(contextService.load("Bearer test-token")).thenReturn(
                new DashboardContext(BigDecimal.valueOf(100), 2, 3, 2)
        );
        when(insightEngine.generate(org.mockito.ArgumentMatchers.any())).thenReturn(
                new CoachingMessageResponse(
                        "GENERAL_TIP",
                        "You are level 2.",
                        InsightEngine.DISCLAIMER
                )
        );

        mockMvc.perform(post("/api/insights/coach-message")
                        .with(jwt().jwt(this::withToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disclaimer").value(InsightEngine.DISCLAIMER));
    }

    private void withToken(Jwt.Builder builder) {
        builder
                .tokenValue("test-token")
                .subject("user-123");
    }
}
