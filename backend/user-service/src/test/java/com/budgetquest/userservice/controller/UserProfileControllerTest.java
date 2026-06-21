package com.budgetquest.userservice.controller;

import com.budgetquest.userservice.dto.UpdateUserProfileRequest;
import com.budgetquest.userservice.dto.UserProfileResponse;
import com.budgetquest.userservice.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    @Test
    void meUsesAuthenticatedJwtSubject() throws Exception {
        String userId = "user-123";
        UUID profileId = UUID.randomUUID();
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        UserProfileResponse response = new UserProfileResponse(
                profileId,
                userId,
                "muberra",
                "USD",
                now,
                now
        );

        when(userProfileService.getOrCreateProfile(userId, "muberra")).thenReturn(response);

        mockMvc.perform(get("/api/users/me").with(jwt().jwt(this::withUserSubject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profileId.toString()))
                .andExpect(jsonPath("$.keycloakUserId").value(userId))
                .andExpect(jsonPath("$.displayName").value("muberra"))
                .andExpect(jsonPath("$.currency").value("USD"));

        verify(userProfileService).getOrCreateProfile(userId, "muberra");
    }

    @Test
    void updateMeUsesAuthenticatedJwtSubject() throws Exception {
        String userId = "user-123";
        UUID profileId = UUID.randomUUID();
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        UserProfileResponse response = new UserProfileResponse(
                profileId,
                userId,
                "Muberra Kurt",
                "EUR",
                now,
                now
        );

        when(userProfileService.updateProfile(any(), any(UpdateUserProfileRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/me")
                        .with(jwt().jwt(this::withUserSubject))
                        .contentType("application/json")
                        .content("""
                                {
                                  "displayName": "Muberra Kurt",
                                  "currency": "EUR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakUserId").value(userId))
                .andExpect(jsonPath("$.displayName").value("Muberra Kurt"))
                .andExpect(jsonPath("$.currency").value("EUR"));

        verify(userProfileService).updateProfile(
                org.mockito.ArgumentMatchers.eq(userId),
                org.mockito.ArgumentMatchers.argThat(request ->
                        request.displayName().equals("Muberra Kurt")
                                && request.currency().equals("EUR")
                )
        );
    }

    @Test
    void meRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    private void withUserSubject(Jwt.Builder builder) {
        builder
                .subject("user-123")
                .claim("preferred_username", "muberra");
    }
}
