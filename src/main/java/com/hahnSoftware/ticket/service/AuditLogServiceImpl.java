package com.hahnSoftware.ticket.service;

import com.hahnSoftware.ticket.entity.AuditLog;
import com.hahnSoftware.ticket.entity.AuditLogDTO;
import com.hahnSoftware.ticket.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private TicketAccessService ticketAccessService;

    @Override
    @Transactional
    public AuditLog addAuditLog(AuditLog auditLog) {
        if (auditLog.getTicket() == null || auditLog.getTicket().getTicketId() == null) {
            throw new IllegalArgumentException("Ticket is required");
        }
        ticketAccessService.requireAccessibleTicket(auditLog.getTicket().getTicketId());
        return auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getAuditLogsByTicketId(Long ticketId) {
        try {
            ticketAccessService.requireAccessibleTicket(ticketId);
            List<AuditLog> auditLogs = auditLogRepository.findByTicket_TicketId(ticketId);
            return auditLogs.stream()
                .map(auditLog -> {
                    AuditLogDTO dto = new AuditLogDTO();
                    dto.setAuditId(auditLog.getAuditId());
                    dto.setTicketId(auditLog.getTicket().getTicketId());
                    dto.setUsername(auditLog.getUser().getUsername());
                    dto.setAction(auditLog.getAction());
                    dto.setOldValue(auditLog.getOldValue());
                    dto.setNewValue(auditLog.getNewValue());
                    dto.setCreatedAt(auditLog.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching audit logs: " + e.getMessage());
        }
    }
}