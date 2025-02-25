package com.hahnSoftware.ticket.service;

import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public Users createUser(Users user) {
        return userRepository.save(user);
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
    public Users updateUser(Long userId, Users userDetails) {
        Users user = getUserById(userId);
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Users findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }
}