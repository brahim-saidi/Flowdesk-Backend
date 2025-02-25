package com.hahnSoftware.ticket.controller;

import com.hahnSoftware.ticket.entity.AuditLog;
import com.hahnSoftware.ticket.entity.AuditLogDTO;
import com.hahnSoftware.ticket.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Log Management", description = "Operations pertaining to audit logs in the Ticket System")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @Operation(summary = "Add a new audit log entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Audit log created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    @PostMapping
    public ResponseEntity<AuditLog> addAuditLog(
            @Parameter(description = "Audit log entry to be created", required = true)
            @RequestBody AuditLog auditLog) {
        AuditLog savedLog = auditLogService.addAuditLog(auditLog);
        return new ResponseEntity<>(savedLog, HttpStatus.CREATED);
    }

    @Operation(summary = "Get audit logs by ticket ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved audit logs"),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized")
    })
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogsByTicketId(
            @Parameter(description = "ID of the ticket to get audit logs for", required = true)
            @PathVariable Long ticketId) {
        List<AuditLogDTO> auditLogs = auditLogService.getAuditLogsByTicketId(ticketId);
        return ResponseEntity.ok(auditLogs);
    }
}