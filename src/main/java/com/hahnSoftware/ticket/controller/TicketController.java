package com.hahnSoftware.ticket.controller;

import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.entity.TicketCreationDTO;
import com.hahnSoftware.ticket.entity.TicketDTO;
import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Ticket Management", description = "Ticket operations")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        logger.info("Initializing TicketController");
        if (ticketService == null) {
            logger.error("TicketService is null");
            throw new IllegalArgumentException("TicketService cannot be null");
        }
        this.ticketService = ticketService;
    }

   

    @PostMapping
    @Operation(summary = "Create a new ticket")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
   public ResponseEntity<Ticket> createTicket(@RequestBody TicketCreationDTO dto) {
    Ticket ticket = new Ticket();
    ticket.setTitle(dto.getTitle());
    ticket.setDescription(dto.getDescription());
    ticket.setPriority(dto.getPriority());
    ticket.setCategory(dto.getCategory());
    ticket.setStatus(dto.getStatus());
    
    Users createdByUser = new Users();
    createdByUser.setUserId(dto.getCreatedByUserId());
    ticket.setCreatedBy(createdByUser);
    
    return ResponseEntity.ok(ticketService.createTicket(ticket));
}

    
    @Operation(summary = "Get ticket by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the ticket"),
        @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long ticketId) {
        try {
            System.out.println("Controller receiving request for ticket: " + ticketId);
            TicketDTO ticket = ticketService.getTicketDTOById(ticketId);
            if (ticket == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            System.err.println("Error in getTicketById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping
    @Operation(summary = "Get all tickets")
    @ApiResponse(responseCode = "200", description = "Retrieved all tickets") 
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        List<TicketDTO> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tickets by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found tickets with specified status"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public ResponseEntity<List<Ticket>> getTicketsByStatus(
            @PathVariable Ticket.Status status) {
        List<Ticket> tickets = ticketService.getTicketsByStatus(status);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Update ticket status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Ticket not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    @PutMapping("/{ticketId}/status")
    public ResponseEntity<Ticket> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam Ticket.Status newStatus) {
        try {
            Ticket updatedTicket = ticketService.updateTicketStatus(ticketId, newStatus);
            return ResponseEntity.ok(updatedTicket);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(Exception e) {
        return ResponseEntity.notFound().build();
    }

    
}