package com.hahnSoftware.ticket.dto;

import java.time.Instant;

import com.hahnSoftware.ticket.entity.Users;

public record AdminUserResponse(
        Long userId,
        String username,
        String email,
        Users.Role role,
        boolean enabled,
        Instant createdAt,
        Instant lastLogin
) {
    public static AdminUserResponse fromEntity(Users u) {
        return new AdminUserResponse(
                u.getUserId(),
                u.getUsername(),
                u.getEmail(),
                u.getRole(),
                u.isEnabled(),
                u.getCreatedAt() != null ? u.getCreatedAt().toInstant() : null,
                u.getLast_login() != null ? u.getLast_login().toInstant() : null);
    }
}
