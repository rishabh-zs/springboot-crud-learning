package com.zs.practice1.model;

/**
 * Login response payload containing the issued JWT.
 */
public record AuthResponse(String token) {
}

