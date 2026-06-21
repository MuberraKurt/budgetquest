package com.budgetquest.userservice.service;

import com.budgetquest.userservice.dto.UpdateUserProfileRequest;
import com.budgetquest.userservice.dto.UserProfileResponse;
import com.budgetquest.userservice.entity.UserProfile;
import com.budgetquest.userservice.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserProfileResponse getOrCreateProfile(String keycloakUserId, String fallbackName) {
        UserProfile profile = repository.findByKeycloakUserId(keycloakUserId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setKeycloakUserId(keycloakUserId);
                    newProfile.setDisplayName(resolveDisplayName(fallbackName));
                    newProfile.setCurrency("USD");
                    return repository.save(newProfile);
                });

        return toResponse(profile);
    }

    @Transactional
    public UserProfileResponse updateProfile(String keycloakUserId, UpdateUserProfileRequest request) {
        UserProfile profile = repository.findByKeycloakUserId(keycloakUserId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setKeycloakUserId(keycloakUserId);
                    return newProfile;
                });

        profile.setDisplayName(request.displayName());
        profile.setCurrency(request.currency().toUpperCase());

        return toResponse(repository.save(profile));
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getKeycloakUserId(),
                profile.getDisplayName(),
                profile.getCurrency(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    private String resolveDisplayName(String fallbackName) {
        if (fallbackName == null || fallbackName.isBlank()) {
            return "BudgetQuest User";
        }

        return fallbackName;
    }
}
