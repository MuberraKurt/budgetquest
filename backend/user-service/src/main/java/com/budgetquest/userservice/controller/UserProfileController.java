package com.budgetquest.userservice.controller;

import com.budgetquest.userservice.dto.UpdateUserProfileRequest;
import com.budgetquest.userservice.dto.UserProfileResponse;
import com.budgetquest.userservice.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public UserProfileResponse me(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String fallbackName = jwt.getClaimAsString("preferred_username");
        return service.getOrCreateProfile(userId, fallbackName);
    }

    @PutMapping("/me")
    public UserProfileResponse updateMe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        return service.updateProfile(jwt.getSubject(), request);
    }
}