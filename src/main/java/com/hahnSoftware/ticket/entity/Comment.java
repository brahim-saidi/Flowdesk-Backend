package com.hahnSoftware.ticket.entity;

import java.sql.Timestamp;


import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq_gen")
    @SequenceGenerator(name = "comment_seq_gen", sequenceName = "comment_seq", allocationSize = 1)
    @Column(name = "comment_id")
    private Long commentId;





    @Column(name = "content")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @ManyToOne
    @JoinColumn(name = "ticket_id", updatable = false)
    @JsonBackReference
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    private Users user;

    // Proper setter for ticket that takes a Ticket object
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}