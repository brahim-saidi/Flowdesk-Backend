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

    @Override
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByTicketId(Long ticketId) {
        // Updated to use the correct repository method
        return commentRepository.findByTicket_TicketId(ticketId);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}