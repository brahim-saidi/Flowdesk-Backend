package com.hahnSoftware.ticket.repository;

import com.hahnSoftware.ticket.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

        @Query("SELECT a FROM AuditLog a JOIN FETCH a.user JOIN FETCH a.ticket WHERE a.ticket.ticketId = :ticketId")
    List<AuditLog> findByTicket_TicketId(@Param("ticketId") Long ticketId);
}