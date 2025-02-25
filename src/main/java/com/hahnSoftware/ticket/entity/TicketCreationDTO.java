package com.hahnSoftware.ticket.entity;

import com.hahnSoftware.ticket.entity.Ticket.Category;
import com.hahnSoftware.ticket.entity.Ticket.Priority;
import com.hahnSoftware.ticket.entity.Ticket.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class TicketCreationDTO {
    private String title;
    private String description;
    private Priority priority;
    private Category category;
    private Status status;
    private Long createdByUserId;  // Just accept the ID instead of the whole object
}