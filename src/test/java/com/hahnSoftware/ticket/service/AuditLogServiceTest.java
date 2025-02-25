package com.hahnSoftware.ticket.service;

import com.hahnSoftware.ticket.entity.AuditLog;
import com.hahnSoftware.ticket.entity.AuditLogDTO;
import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private AuditLog auditLog1;
    private AuditLog auditLog2;
    private Ticket ticket;
    private Users user;

    @BeforeEach
    public void setup() {
        // Set up test data
        ticket = new Ticket();
        ticket.setTicketId(1L);
        ticket.setTitle("Test Ticket");

        user = new Users();
        user.setUserId(1L);
        user.setUsername("testUser");

        auditLog1 = new AuditLog();
        auditLog1.setAuditId(1L);
        auditLog1.setTicket(ticket);
        auditLog1.setUser(user);
        auditLog1.setAction("STATUS_CHANGE");
        auditLog1.setOldValue("OPEN");
        auditLog1.setNewValue("IN_PROGRESS");
        auditLog1.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        auditLog2 = new AuditLog();
        auditLog2.setAuditId(2L);
        auditLog2.setTicket(ticket);
        auditLog2.setUser(user);
        auditLog2.setAction("COMMENT_ADDED");
        auditLog2.setOldValue(null);
        auditLog2.setNewValue("New comment");
        auditLog2.setCreatedAt(new Timestamp(System.currentTimeMillis() + 3600000)); // Plus 1 hour in milliseconds
    }

    @Test
    public void testAddAuditLog() {
        // Arrange
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog1);

        // Act
        AuditLog savedAuditLog = auditLogService.addAuditLog(auditLog1);

        // Assert
        assertNotNull(savedAuditLog);
        assertEquals(auditLog1.getAuditId(), savedAuditLog.getAuditId());
        assertEquals(auditLog1.getAction(), savedAuditLog.getAction());
        assertEquals(auditLog1.getOldValue(), savedAuditLog.getOldValue());
        assertEquals(auditLog1.getNewValue(), savedAuditLog.getNewValue());
        
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    public void testGetAuditLogsByTicketId() {
        // Arrange
        List<AuditLog> auditLogs = Arrays.asList(auditLog1, auditLog2);
        when(auditLogRepository.findByTicket_TicketId(1L)).thenReturn(auditLogs);

        // Act
        List<AuditLogDTO> dtoList = auditLogService.getAuditLogsByTicketId(1L);

        // Assert
        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        
        // Verify first DTO
        AuditLogDTO dto1 = dtoList.get(0);
        assertEquals(auditLog1.getAuditId(), dto1.getAuditId());
        assertEquals(auditLog1.getTicket().getTicketId(), dto1.getTicketId());
        assertEquals(auditLog1.getUser().getUsername(), dto1.getUsername());
        assertEquals(auditLog1.getAction(), dto1.getAction());
        assertEquals(auditLog1.getOldValue(), dto1.getOldValue());
        assertEquals(auditLog1.getNewValue(), dto1.getNewValue());
        assertEquals(auditLog1.getCreatedAt(), dto1.getCreatedAt());
        
        // Verify second DTO
        AuditLogDTO dto2 = dtoList.get(1);
        assertEquals(auditLog2.getAuditId(), dto2.getAuditId());
        assertEquals("COMMENT_ADDED", dto2.getAction());
        
        verify(auditLogRepository, times(1)).findByTicket_TicketId(1L);
    }

    @Test
    public void testGetAuditLogsByTicketId_Exception() {
        
        when(auditLogRepository.findByTicket_TicketId(1L)).thenThrow(new RuntimeException("Database error"));
    
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            auditLogService.getAuditLogsByTicketId(1L);
        });
        
        assertTrue(exception.getMessage().contains("Error fetching audit logs: Database error"));
        verify(auditLogRepository, times(1)).findByTicket_TicketId(1L);
    }
}