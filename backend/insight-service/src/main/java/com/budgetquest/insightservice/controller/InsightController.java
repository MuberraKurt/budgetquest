package com.budgetquest.insightservice.controller;

import com.budgetquest.insightservice.dto.CoachingMessageResponse;
import com.budgetquest.insightservice.dto.DashboardContext;
import com.budgetquest.insightservice.service.DashboardContextService;
import com.budgetquest.insightservice.service.InsightEngine;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insights")
public class InsightController {

    private final DashboardContextService contextService;
    private final InsightEngine insightEngine;

    public InsightController(
            DashboardContextService contextService,
            InsightEngine insightEngine
    ) {
        this.contextService = contextService;
        this.insightEngine = insightEngine;
    }

    @GetMapping("/coaching")
    public CoachingMessageResponse coaching(@AuthenticationPrincipal Jwt jwt) {
        return generateMessage(jwt);
    }

    @GetMapping("/dashboard")
    public CoachingMessageResponse dashboard(@AuthenticationPrincipal Jwt jwt) {
        return generateMessage(jwt);
    }

    @PostMapping("/coach-message")
    public CoachingMessageResponse coachMessage(@AuthenticationPrincipal Jwt jwt) {
        return generateMessage(jwt);
    }

    private CoachingMessageResponse generateMessage(Jwt jwt) {
        String authorizationHeader = "Bearer " + jwt.getTokenValue();
        DashboardContext context = contextService.load(authorizationHeader);
        return insightEngine.generate(context);
    }
}
