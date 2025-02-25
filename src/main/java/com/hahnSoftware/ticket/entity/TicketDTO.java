package com.hahnSoftware.ticket.entity;

import java.sql.Timestamp;
import java.util.List;

import com.hahnSoftware.ticket.entity.Ticket.Category;
import com.hahnSoftware.ticket.entity.Ticket.Priority;
import com.hahnSoftware.ticket.entity.Ticket.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    private Long ticketId;
    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private Status status;
    private Timestamp createdAt;
    
    // Instead of whole User object, just send necessary user information
    private UserSummaryDTO assignedUser;
    private UserSummaryDTO createdBy;
    
    // For comments, we might want to create a separate CommentDTO
    private List<CommentDTO> comments;


    
}