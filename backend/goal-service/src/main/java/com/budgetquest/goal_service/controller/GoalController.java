package com.budgetquest.goal_service.controller;

import com.budgetquest.goal_service.dto.GoalRequest;
import com.budgetquest.goal_service.dto.GoalResponse;
import com.budgetquest.goal_service.entity.GoalStatus;
import com.budgetquest.goal_service.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    private final GoalService service;

    public GoalController(GoalService service) {
        this.service = service;
    }

    @GetMapping
    public List<GoalResponse> list(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) GoalStatus status
    ) {
        return service.list(jwt.getSubject(), status);
    }

    @GetMapping("/{id}")
    public GoalResponse get(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        return service.get(jwt.getSubject(), id);
    }

    @PostMapping
    public GoalResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody GoalRequest request
    ) {
        return service.create(jwt.getSubject(), request);
    }

    @PutMapping("/{id}")
    public GoalResponse update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody GoalRequest request
    ) {
        return service.update(jwt.getSubject(), id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id
    ) {
        service.delete(jwt.getSubject(), id);
    }
}