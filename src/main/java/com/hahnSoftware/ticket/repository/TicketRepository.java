package com.hahnSoftware.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hahnSoftware.ticket.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    List<Ticket> findByStatus(Ticket.Status status);

    @Query("""
            SELECT t FROM Ticket t
            WHERE t.createdBy.userId = :userId
               OR (t.assignedUser IS NOT NULL AND t.assignedUser.userId = :userId)
            """)
    List<Ticket> findVisibleToUser(@Param("userId") Long userId);

    @Query("""
            SELECT t FROM Ticket t
            WHERE t.status = :status
              AND (t.createdBy.userId = :userId
                   OR (t.assignedUser IS NOT NULL AND t.assignedUser.userId = :userId))
            """)
    List<Ticket> findByStatusVisibleToUser(@Param("status") Ticket.Status status, @Param("userId") Long userId);
}
