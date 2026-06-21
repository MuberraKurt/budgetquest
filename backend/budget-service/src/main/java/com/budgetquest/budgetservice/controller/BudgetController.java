package com.budgetquest.budgetservice.controller;

import com.budgetquest.budgetservice.dto.MonthlyBudgetSummaryResponse;
import com.budgetquest.budgetservice.dto.YearlyBudgetSummaryResponse;
import com.budgetquest.budgetservice.service.BudgetSummaryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetSummaryService budgetSummaryService;

    public BudgetController(BudgetSummaryService budgetSummaryService) {
        this.budgetSummaryService = budgetSummaryService;
    }

    @GetMapping("/monthly")
    public MonthlyBudgetSummaryResponse monthly(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return budgetSummaryService.getMonthlySummary(jwt.getSubject(), year, month);
    }

    @GetMapping("/yearly")
    public YearlyBudgetSummaryResponse yearly(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam int year
    ) {
        return budgetSummaryService.getYearlySummary(jwt.getSubject(), year);
    }
}