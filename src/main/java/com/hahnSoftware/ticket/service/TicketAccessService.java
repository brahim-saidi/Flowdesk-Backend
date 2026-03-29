package com.hahnSoftware.ticket.service;

import org.springframework.stereotype.Service;

import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.repository.TicketRepository;
import com.hahnSoftware.ticket.security.CurrentUserService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class TicketAccessService {

    private final TicketRepository ticketRepository;
    private final CurrentUserService currentUserService;

    public TicketAccessService(TicketRepository ticketRepository, CurrentUserService currentUserService) {
        this.ticketRepository = ticketRepository;
        this.currentUserService = currentUserService;
    }

    /**
     * Returns the ticket if the current user may access it (IT_SUPPORT: any; EMPLOYEE: creator or assignee).
     * Uses 404 when forbidden so ticket IDs are not leaked.
     */
    public Ticket requireAccessibleTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));
        if (currentUserService.isItSupport()) {
            return ticket;
        }
        Long userId = currentUserService.requireUserId();
        if (isUserInvolved(ticket, userId)) {
            return ticket;
        }
        throw new EntityNotFoundException("Ticket not found with id: " + ticketId);
    }

    public boolean isUserInvolved(Ticket ticket, Long userId) {
        if (ticket.getCreatedBy() != null && userId.equals(ticket.getCreatedBy().getUserId())) {
            return true;
        }
        return ticket.getAssignedUser() != null && userId.equals(ticket.getAssignedUser().getUserId());
    }
}
