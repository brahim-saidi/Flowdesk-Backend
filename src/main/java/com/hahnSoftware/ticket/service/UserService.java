package com.hahnSoftware.ticket.service;

import java.util.List;

import com.hahnSoftware.ticket.entity.Users;




public interface UserService {
    Users createUser(Users user);
    Users getUserById(Long id);
    List<Users> getAllUsers();
    Users updateUser(Long id, Users user);
    void deleteUser(Long id);
    Users findByUsername(String username);
}