package com.hahnSoftware.ticket.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.time.Instant;

@Component

@Scope("prototype") 
public class Login {
    @PersistenceContext
    private EntityManager entityManager;
    
    private String username;
    private String password;
    private Users.Role role;
    private Long userId;

    public Login() {
        // Default constructor for Spring
    }

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Transactional
    public boolean authenticate() {
        System.out.println("Starting database authentication for user: " + username);
        
        try {
            String jpql = "SELECT u FROM Users u WHERE u.username = :username AND u.password = :password";
            TypedQuery<Users> query = entityManager.createQuery(jpql, Users.class);
            query.setParameter("username", username);
            query.setParameter("password", password);

            Users user = query.getSingleResult();
            
            if (user != null) {
                this.role = user.getRole();
                this.userId = user.getUserId();
                
                // Update last login time
                user.setLast_login(Timestamp.from(Instant.now()));
                entityManager.merge(user);
                
                System.out.println("Authentication successful for user: " + username + " with role: " + role);
                return true;
            }
        } catch (NoResultException e) {
            System.out.println("No user found with provided credentials");
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Users.Role getRole() {
        return role;
    }

    public Long getUserId() {
        return userId;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}