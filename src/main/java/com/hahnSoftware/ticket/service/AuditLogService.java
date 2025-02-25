package com.hahnSoftware.ticket.service;

import com.hahnSoftware.ticket.entity.AuditLog;
import com.hahnSoftware.ticket.entity.AuditLogDTO;
import java.util.List;

public interface AuditLogService {
    AuditLog addAuditLog(AuditLog auditLog);
    List<AuditLogDTO> getAuditLogsByTicketId(Long ticketId);
}