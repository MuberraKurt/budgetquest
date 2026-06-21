package com.budgetquest.gamificationservice.controller;

import com.budgetquest.gamificationservice.dto.BadgeResponse;
import com.budgetquest.gamificationservice.dto.GamificationProfileResponse;
import com.budgetquest.gamificationservice.dto.StreakResponse;
import com.budgetquest.gamificationservice.service.GamificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gamification")
public class GamificationController {

    private final GamificationService service;

    public GamificationController(GamificationService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public GamificationProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        return service.getProfile(jwt.getSubject());
    }

    @GetMapping("/profile")
    public GamificationProfileResponse profile(@AuthenticationPrincipal Jwt jwt) {
        return service.getProfile(jwt.getSubject());
    }

    @GetMapping("/me/badges")
    public List<BadgeResponse> badges(@AuthenticationPrincipal Jwt jwt) {
        return service.getBadges(jwt.getSubject());
    }

    @GetMapping("/badges")
    public List<BadgeResponse> roadmapBadges(@AuthenticationPrincipal Jwt jwt) {
        return service.getBadges(jwt.getSubject());
    }

    @GetMapping("/streaks")
    public StreakResponse streaks(@AuthenticationPrincipal Jwt jwt) {
        GamificationProfileResponse profile = service.getProfile(jwt.getSubject());
        return new StreakResponse(profile.userId(), profile.currentStreak());
    }
}
