package com.hahnSoftware.ticket.entity;



import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter
public class Users {
     @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long userId;  
    
    private String username;
    private String password;
    private String email;
   


     @Enumerated(EnumType.STRING)
     private Role role = Role.EMPLOYEE; // Default role
        


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    private Timestamp last_login;


    @OneToMany(mappedBy = "assignedUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("assignedUser") // Ignore the assignedUser field in Ticket to avoid recursion
    private List<Ticket> assignedTickets;

      @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonIgnoreProperties("createdBy") // Ignore the createdBy field in Ticket to avoid recursion
     private List<Ticket> createdTickets;

     @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
     private List<AuditLog> auditLogs;

     public enum Role { 
        EMPLOYEE, 
        IT_SUPPORT
    }




}