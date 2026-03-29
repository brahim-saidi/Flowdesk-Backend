package com.hahnSoftware.ticket.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hahnSoftware.ticket.dto.AdminUserResponse;
import com.hahnSoftware.ticket.dto.UserCreateRequest;
import com.hahnSoftware.ticket.entity.Users;

public interface UserService {
    Users createUser(Users user);

    AdminUserResponse createUserByAdmin(UserCreateRequest request);

    Page<AdminUserResponse> listUsersForAdmin(Pageable pageable);

    AdminUserResponse setUserEnabled(Long userId, boolean enabled);

    void resetPasswordByAdmin(Long userId, String newPassword);

    Users getUserById(Long id);

    List<Users> getAllUsers();

    Users updateUser(Long id, Users user);
}
