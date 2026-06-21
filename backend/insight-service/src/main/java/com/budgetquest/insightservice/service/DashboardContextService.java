package com.budgetquest.insightservice.service;

import com.budgetquest.insightservice.dto.BudgetSummaryDto;
import com.budgetquest.insightservice.dto.DashboardContext;
import com.budgetquest.insightservice.dto.GamificationProfileDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DashboardContextService {

    private final WebClient.Builder webClientBuilder;

    public DashboardContextService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public DashboardContext load(String authorizationHeader) {
        LocalDate today = LocalDate.now();

        BudgetSummaryDto budget = fetchBudget(authorizationHeader, today.getYear(), today.getMonthValue());
        GamificationProfileDto gamification = fetchGamification(authorizationHeader);

        return new DashboardContext(
                budget != null ? budget.balance() : BigDecimal.ZERO,
                gamification != null ? gamification.currentStreak() : 0,
                gamification != null ? gamification.totalTransactions() : 0,
                gamification != null ? gamification.level() : 1
        );
    }

    private BudgetSummaryDto fetchBudget(String authorizationHeader, int year, int month) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://budget-service/api/budgets/monthly?year={year}&month={month}", year, month)
                    .header("Authorization", authorizationHeader)
                    .retrieve()
                    .bodyToMono(BudgetSummaryDto.class)
                    .block();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private GamificationProfileDto fetchGamification(String authorizationHeader) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://gamification-service/api/gamification/me")
                    .header("Authorization", authorizationHeader)
                    .retrieve()
                    .bodyToMono(GamificationProfileDto.class)
                    .block();
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
