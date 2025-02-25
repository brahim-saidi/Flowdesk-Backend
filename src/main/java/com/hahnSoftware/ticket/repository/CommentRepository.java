package com.hahnSoftware.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hahnSoftware.ticket.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicket_TicketId(Long ticketId);
}