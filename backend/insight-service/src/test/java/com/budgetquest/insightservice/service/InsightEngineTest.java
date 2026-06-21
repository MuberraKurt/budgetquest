package com.budgetquest.insightservice.service;

import com.budgetquest.insightservice.dto.CoachingMessageResponse;
import com.budgetquest.insightservice.dto.DashboardContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InsightEngineTest {

    private final InsightEngine engine = new InsightEngine();

    @Test
    void negativeBalanceReturnsSpendingAwareness() {
        DashboardContext context = new DashboardContext(
                BigDecimal.valueOf(-50),
                0,
                5,
                2
        );

        CoachingMessageResponse response = engine.generate(context);

        assertThat(response.code()).isEqualTo("SPENDING_AWARENESS");
        assertThat(response.disclaimer()).isEqualTo(InsightEngine.DISCLAIMER);
    }

    @Test
    void longStreakReturnsCelebration() {
        DashboardContext context = new DashboardContext(
                BigDecimal.valueOf(100),
                7,
                5,
                3
        );

        CoachingMessageResponse response = engine.generate(context);

        assertThat(response.code()).isEqualTo("STREAK_CELEBRATION");
        assertThat(response.message()).contains("7");
    }

    @Test
    void noTransactionsReturnsGetStarted() {
        DashboardContext context = new DashboardContext(
                BigDecimal.ZERO,
                0,
                0,
                1
        );

        CoachingMessageResponse response = engine.generate(context);

        assertThat(response.code()).isEqualTo("GET_STARTED");
    }

    @Test
    void defaultReturnsGeneralTip() {
        DashboardContext context = new DashboardContext(
                BigDecimal.valueOf(250),
                2,
                4,
                3
        );

        CoachingMessageResponse response = engine.generate(context);

        assertThat(response.code()).isEqualTo("GENERAL_TIP");
        assertThat(response.message()).contains("level 3");
    }
}
