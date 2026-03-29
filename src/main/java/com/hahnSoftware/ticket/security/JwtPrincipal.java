package com.hahnSoftware.ticket.security;

public record JwtPrincipal(String username, Long userId, String role) {}
