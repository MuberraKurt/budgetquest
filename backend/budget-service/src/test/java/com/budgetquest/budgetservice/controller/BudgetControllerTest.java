package com.budgetquest.budgetservice.controller;

import com.budgetquest.budgetservice.dto.MonthlyBudgetSummaryResponse;
import com.budgetquest.budgetservice.service.BudgetSummaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BudgetController.class)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BudgetSummaryService budgetSummaryService;

    @Test
    void monthlyUsesAuthenticatedJwtSubject() throws Exception {
        MonthlyBudgetSummaryResponse response = new MonthlyBudgetSummaryResponse(
                "user-123",
                2026,
                6,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(250),
                BigDecimal.valueOf(750),
                BigDecimal.valueOf(750),
                BigDecimal.valueOf(75),
                BigDecimal.valueOf(750),
                List.of()
        );

        when(budgetSummaryService.getMonthlySummary("user-123", 2026, 6))
                .thenReturn(response);

        mockMvc.perform(get("/api/budgets/monthly")
                        .param("year", "2026")
                        .param("month", "6")
                        .with(jwt().jwt(this::withSubject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user-123"))
                .andExpect(jsonPath("$.year").value(2026))
                .andExpect(jsonPath("$.month").value(6))
                .andExpect(jsonPath("$.incomeTotal").value(1000))
                .andExpect(jsonPath("$.expenseTotal").value(250))
                .andExpect(jsonPath("$.balance").value(750));

        verify(budgetSummaryService).getMonthlySummary("user-123", 2026, 6);
    }

    @Test
    void monthlyRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/budgets/monthly")
                        .param("year", "2026")
                        .param("month", "6"))
                .andExpect(status().isUnauthorized());
    }

    private void withSubject(Jwt.Builder builder) {
        builder.subject("user-123");
    }
}
