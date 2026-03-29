package com.hahnSoftware.ticket.dto;

import com.hahnSoftware.ticket.entity.Users;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInMs,
        Long userId,
        String username,
        Users.Role role
) {}
