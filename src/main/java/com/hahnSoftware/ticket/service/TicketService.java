package com.hahnSoftware.ticket.service;

import java.util.List;

import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.entity.TicketDTO;



public interface TicketService {
    Ticket createTicket(Ticket ticket);
    Ticket updateTicketStatus(Long ticketId, Ticket.Status status);
    List<TicketDTO> getAllTickets();
    Ticket getTicketById(Long ticketId);
    List<Ticket> getTicketsByStatus(Ticket.Status status);


    TicketDTO getTicketDTOById(Long ticketId);
}
