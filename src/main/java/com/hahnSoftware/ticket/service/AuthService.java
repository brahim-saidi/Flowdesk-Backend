package com.hahnSoftware.ticket.service;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hahnSoftware.ticket.dto.LoginResponse;
import com.hahnSoftware.ticket.dto.MeResponse;
import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.repository.UserRepository;
import com.hahnSoftware.ticket.security.JwtService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public LoginResponse login(String username, String password) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Invalid credentials");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        user.setLast_login(Timestamp.from(Instant.now()));
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new LoginResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                user.getUserId(),
                user.getUsername(),
                user.getRole());
    }

    @Transactional(readOnly = true)
    public MeResponse getCurrentUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return new MeResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled());
    }
}
