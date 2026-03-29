package com.hahnSoftware.ticket.dto;

import com.hahnSoftware.ticket.entity.Users;

public record MeResponse(
        Long userId,
        String username,
        String email,
        Users.Role role,
        boolean enabled
) {}
