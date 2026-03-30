package com.hahnSoftware.ticket.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hahnSoftware.ticket.dto.AdminPasswordResetRequest;
import com.hahnSoftware.ticket.dto.AdminUserResponse;
import com.hahnSoftware.ticket.dto.UserCreateRequest;
import com.hahnSoftware.ticket.dto.UserEnabledRequest;
import com.hahnSoftware.ticket.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('IT_SUPPORT')")
@Tag(name = "Admin — Users", description = "IT support only: manage users")
@SecurityRequirement(name = "bearer-jwt")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "List users (paged, sortable)")
    public ResponseEntity<Page<AdminUserResponse>> listUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.listUsersForAdmin(pageable));
    }

    @PostMapping
    @Operation(summary = "Create a new user (password stored as BCrypt)")
    public ResponseEntity<AdminUserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        AdminUserResponse created = userService.createUserByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{userId}/enabled")
    @Operation(summary = "Enable or disable a user account")
    public ResponseEntity<AdminUserResponse> setEnabled(
            @PathVariable Long userId,
            @Valid @RequestBody UserEnabledRequest request) {
        return ResponseEntity.ok(userService.setUserEnabled(userId, request.enabled()));
    }

    @PostMapping("/{userId}/reset-password")
    @Operation(summary = "Reset a user's password (BCrypt)")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long userId,
            @Valid @RequestBody AdminPasswordResetRequest request) {
        userService.resetPasswordByAdmin(userId, request.newPassword());
        return ResponseEntity.noContent().build();
    }
}




