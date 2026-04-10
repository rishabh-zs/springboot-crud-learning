package com.zs.practice1.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Login request payload.
 */
public record AuthRequest(
        @NotBlank(message = "Username is required") String username,
        @NotBlank(message = "Password is required") String password
) {
}

