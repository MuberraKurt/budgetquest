package com.budgetquest.gamificationservice.service;

import com.budgetquest.gamificationservice.dto.BadgeResponse;
import com.budgetquest.gamificationservice.dto.GamificationProfileResponse;
import com.budgetquest.gamificationservice.entity.UserBadge;
import com.budgetquest.gamificationservice.entity.UserGamification;
import com.budgetquest.gamificationservice.event.TransactionChangedEvent;
import com.budgetquest.gamificationservice.repository.UserBadgeRepository;
import com.budgetquest.gamificationservice.repository.UserGamificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GamificationService {

    private final UserGamificationRepository gamificationRepository;
    private final UserBadgeRepository badgeRepository;
    private final GamificationRulesService rules;

    public GamificationService(
            UserGamificationRepository gamificationRepository,
            UserBadgeRepository badgeRepository,
            GamificationRulesService rules
    ) {
        this.gamificationRepository = gamificationRepository;
        this.badgeRepository = badgeRepository;
        this.rules = rules;
    }

    @Transactional
    public void handleTransactionEvent(TransactionChangedEvent event) {
        if ("CREATED".equals(event.action())) {
            handleCreatedEvent(event);
            return;
        }

        if ("UPDATED".equals(event.action())) {
            handleUpdatedEvent(event);
            return;
        }

        if ("DELETED".equals(event.action())) {
            handleDeletedEvent(event);
        }
    }

    private void handleCreatedEvent(TransactionChangedEvent event) {
        UserGamification profile = gamificationRepository
                .findById(event.userId())
                .orElseGet(() -> {
                    UserGamification created = new UserGamification();
                    created.setUserId(event.userId());
                    return created;
                });

        profile.setTotalTransactions(profile.getTotalTransactions() + 1);
        profile.setXp(profile.getXp() + rules.xpForEvent(event));
        profile.setLevel(rules.levelFromXp(profile.getXp()));

        LocalDate today = event.transactionDate();
        profile.setCurrentStreak(
                rules.updateStreak(profile.getCurrentStreak(), profile.getLastActiveDate(), today)
        );
        profile.setLastActiveDate(today);

        gamificationRepository.save(profile);

        rules.badgeForTransactionCount(profile.getTotalTransactions())
                .ifPresent(badgeCode -> awardBadgeIfMissing(event.userId(), badgeCode));
    }

    private void handleUpdatedEvent(TransactionChangedEvent event) {
        UserGamification profile = getOrCreateProfile(event.userId());
        int previousXp = event.previousType() == null ? 0 : rules.xpForType(event.previousType());
        int currentXp = rules.xpForType(event.type());

        profile.setXp(Math.max(0, profile.getXp() + currentXp - previousXp));
        profile.setLevel(rules.levelFromXp(profile.getXp()));

        gamificationRepository.save(profile);
    }

    private void handleDeletedEvent(TransactionChangedEvent event) {
        UserGamification profile = getOrCreateProfile(event.userId());
        profile.setTotalTransactions(Math.max(0, profile.getTotalTransactions() - 1));
        gamificationRepository.save(profile);
    }

    private UserGamification getOrCreateProfile(String userId) {
        return gamificationRepository
                .findById(userId)
                .orElseGet(() -> {
                    UserGamification created = new UserGamification();
                    created.setUserId(userId);
                    return created;
                });
    }

    private void awardBadgeIfMissing(String userId, String badgeCode) {
        if (badgeRepository.findByUserIdAndBadgeCode(userId, badgeCode).isPresent()) {
            return;
        }

        UserBadge badge = new UserBadge();
        badge.setUserId(userId);
        badge.setBadgeCode(badgeCode);
        badgeRepository.save(badge);
    }

    @Transactional(readOnly = true)
    public GamificationProfileResponse getProfile(String userId) {
        UserGamification profile = gamificationRepository
                .findById(userId)
                .orElseGet(() -> {
                    UserGamification empty = new UserGamification();
                    empty.setUserId(userId);
                    return empty;
                });

        return new GamificationProfileResponse(
                profile.getUserId(),
                profile.getXp(),
                profile.getLevel(),
                profile.getCurrentStreak(),
                profile.getTotalTransactions()
        );
    }

    @Transactional(readOnly = true)
    public List<BadgeResponse> getBadges(String userId) {
        return badgeRepository.findByUserIdOrderByEarnedAtDesc(userId)
                .stream()
                .map(b -> new BadgeResponse(b.getBadgeCode(), b.getEarnedAt()))
                .toList();
    }
}
