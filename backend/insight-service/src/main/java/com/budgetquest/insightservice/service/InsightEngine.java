package com.budgetquest.insightservice.service;

import com.budgetquest.insightservice.dto.CoachingMessageResponse;
import com.budgetquest.insightservice.dto.DashboardContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InsightEngine {

    public static final String DISCLAIMER =
            "This is educational information, not financial advice.";

    public CoachingMessageResponse generate(DashboardContext context) {
        if (context.monthlyBalance().compareTo(BigDecimal.ZERO) < 0) {
            return new CoachingMessageResponse(
                    "SPENDING_AWARENESS",
                    "Your expenses are higher than income this month. " +
                    "Review your top categories and look for one small cut you can make next week.",
                    DISCLAIMER
            );
        }

        if (context.currentStreak() >= 7) {
            return new CoachingMessageResponse(
                    "STREAK_CELEBRATION",
                    "Strong habit streak: " + context.currentStreak() +
                    " days of activity. Consistency is one of the best predictors of progress.",
                    DISCLAIMER
            );
        }

        if (context.totalTransactions() == 0) {
            return new CoachingMessageResponse(
                    "GET_STARTED",
                    "Start by logging one transaction today. Small tracking habits compound over time.",
                    DISCLAIMER
            );
        }

        return new CoachingMessageResponse(
                "GENERAL_TIP",
                "You are level " + context.level() +
                ". Try setting a weekly check-in to review spending and goals together.",
                DISCLAIMER
        );
    }
}
