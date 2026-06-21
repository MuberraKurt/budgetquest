package com.budgetquest.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @NotBlank
        @Size(max = 80)
        String displayName,

        @NotBlank
        @Size(min = 3, max = 3)
        @Pattern(
                regexp = "^[A-Za-z]{3}$",
                message = "Currency must be a 3-letter code"
        )
        String currency
) {
}