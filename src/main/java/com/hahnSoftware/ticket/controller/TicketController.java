package com.hahnSoftware.ticket.controller;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.entity.TicketCreationDTO;
import com.hahnSoftware.ticket.entity.TicketDTO;
import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.security.CurrentUserService;
import com.hahnSoftware.ticket.service.TicketService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Ticket Management", description = "Ticket operations")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private final TicketService ticketService;
    private final CurrentUserService currentUserService;

    @Autowired
    public TicketController(TicketService ticketService, CurrentUserService currentUserService) {
        logger.info("Initializing TicketController");
        if (ticketService == null) {
            logger.error("TicketService is null");
            throw new IllegalArgumentException("TicketService cannot be null");
        }
        this.ticketService = ticketService;
        this.currentUserService = currentUserService;
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

        Long creatorId = dto.getCreatedByUserId();
        if (!currentUserService.isItSupport()) {
            creatorId = currentUserService.requireUserId();
        }
        Users createdByUser = new Users();
        createdByUser.setUserId(creatorId);
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
        TicketDTO ticket = ticketService.getTicketDTOById(ticketId);
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @GetMapping
    @Operation(summary = "Search tickets (paged, sortable). IT support: all tickets. Employees: only tickets they created or are assigned to.")
    @ApiResponse(responseCode = "200", description = "Page of tickets")
    public ResponseEntity<Page<TicketDTO>> searchTickets(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo,
            @RequestParam(required = false) Long assigneeUserId) {
        Page<TicketDTO> page = ticketService.searchTickets(title, createdFrom, createdTo, assigneeUserId, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tickets by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found tickets with specified status"),
            @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public ResponseEntity<List<Ticket>> getTicketsByStatus(@PathVariable Ticket.Status status) {
        return ResponseEntity.ok(ticketService.getTicketsByStatus(status));
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
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId, newStatus));
    }
}
