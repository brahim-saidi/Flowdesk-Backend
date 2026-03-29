package com.hahnSoftware.ticket.service;


import com.hahnSoftware.ticket.entity.Comment;
import com.hahnSoftware.ticket.repository.CommentRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TicketAccessService ticketAccessService;

    @Override
    public Comment addComment(Comment comment) {
        if (comment.getTicket() == null || comment.getTicket().getTicketId() == null) {
            throw new IllegalArgumentException("Ticket is required");
        }
        ticketAccessService.requireAccessibleTicket(comment.getTicket().getTicketId());
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByTicketId(Long ticketId) {
        ticketAccessService.requireAccessibleTicket(ticketId);
        return commentRepository.findByTicket_TicketId(ticketId);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}