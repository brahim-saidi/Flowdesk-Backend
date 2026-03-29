package com.hahnSoftware.ticket.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hahnSoftware.ticket.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);
}