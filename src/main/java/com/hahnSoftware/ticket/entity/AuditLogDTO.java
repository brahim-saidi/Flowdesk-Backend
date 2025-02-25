package com.hahnSoftware.ticket.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private Long auditId;
    private Long ticketId;
    private String username; 
    private String action;
    private String oldValue;
    private String newValue;
    private Timestamp createdAt;
}