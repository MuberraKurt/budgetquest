package com.budgetquest.gamificationservice.repository;

import com.budgetquest.gamificationservice.entity.UserGamification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGamificationRepository extends JpaRepository<UserGamification, String> {
}