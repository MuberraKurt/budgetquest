package com.budgetquest.userservice.repository;

import com.budgetquest.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByKeycloakUserId(String keycloakUserId);
}