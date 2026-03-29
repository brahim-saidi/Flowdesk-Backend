package com.hahnSoftware.ticket.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.entity.TicketDTO;

public interface TicketService {
    Ticket createTicket(Ticket ticket);
    Ticket updateTicketStatus(Long ticketId, Ticket.Status status);
    List<TicketDTO> getAllTickets();

    Page<TicketDTO> searchTickets(
            String title,
            Instant createdFrom,
            Instant createdTo,
            Long assigneeUserId,
            Pageable pageable);
    Ticket getTicketById(Long ticketId);
    List<Ticket> getTicketsByStatus(Ticket.Status status);


    TicketDTO getTicketDTOById(Long ticketId);
}
