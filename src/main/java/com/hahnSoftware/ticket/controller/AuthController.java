package com.hahnSoftware.ticket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hahnSoftware.ticket.dto.LoginRequest;
import com.hahnSoftware.ticket.dto.LoginResponse;
import com.hahnSoftware.ticket.dto.MeResponse;
import com.hahnSoftware.ticket.security.CurrentUserService;
import com.hahnSoftware.ticket.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login and tokens")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login and obtain JWT")
    @SecurityRequirements
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.username(), request.password()));
    }

    @GetMapping("/me")
    @Operation(summary = "Current user from JWT")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<MeResponse> me() {
        return ResponseEntity.ok(authService.getCurrentUser(currentUserService.requireUserId()));
    }
}


