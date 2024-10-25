package com.github.frank.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author Frank An
 */
public record LoginRequest(@NotBlank(message = "Username is required")
                           @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
                           String username,
                           @NotBlank(message = "Password is required")
                           @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
                           String password) {
}
