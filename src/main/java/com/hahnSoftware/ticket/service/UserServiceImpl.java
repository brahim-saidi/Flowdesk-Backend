package com.hahnSoftware.ticket.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hahnSoftware.ticket.dto.AdminUserResponse;
import com.hahnSoftware.ticket.dto.UserCreateRequest;
import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.exception.ConflictException;
import com.hahnSoftware.ticket.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Users createUser(Users user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public AdminUserResponse createUserByAdmin(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists");
        }
        Users user = new Users();
        user.setUsername(request.username().trim());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email().trim());
        user.setRole(request.role());
        user.setEnabled(true);
        Users saved = userRepository.save(user);
        return AdminUserResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> listUsersForAdmin(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserResponse::fromEntity);
    }

    @Override
    @Transactional
    public AdminUserResponse setUserEnabled(Long userId, boolean enabled) {
        Users user = getUserById(userId);
        user.setEnabled(enabled);
        return AdminUserResponse.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public void resetPasswordByAdmin(Long userId, String newPassword) {
        Users user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public Users updateUser(Long userId, Users userDetails) {
        Users user = getUserById(userId);
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }
}