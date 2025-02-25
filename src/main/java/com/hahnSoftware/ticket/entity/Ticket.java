package com.hahnSoftware.ticket.entity;



import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tickets")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_seq_gen")
    @SequenceGenerator(name = "ticket_seq_gen", sequenceName = "ticket_seq", allocationSize = 1)
    @Column(name = "ticket_id")
    private Long ticketId;
    
    private String title;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @Enumerated(EnumType.STRING)
    private Category category;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.EAGER) // Change to EAGER
    @JoinColumn(name = "assigned_to")
    @JsonIgnoreProperties("assignedTickets") // Avoid recursion
    private Users assignedUser;

    @OneToMany(mappedBy = "ticket")
    private List<Comment> comments;


    @ManyToOne(fetch = FetchType.EAGER)  // Change to EAGER temporarily for testing
    @JoinColumn(name = "CREATED_BY", nullable = false)
    @JsonIgnore
    private Users createdBy;


     @OneToMany(mappedBy = "ticket", fetch = FetchType.LAZY)
    private List<AuditLog> auditLogs;





    
    public enum Priority { LOW, MEDIUM, HIGH }
    public enum Category { NETWORK, HARDWARE, SOFTWARE, OTHER }
    public enum Status { NEW, IN_PROGRESS, RESOLVED }


}

