package com.hahnSoftware.ticket.service;

import java.util.List;

import com.hahnSoftware.ticket.entity.Comment;

public interface CommentService {
 Comment addComment(Comment comment);
    List<Comment> getCommentsByTicketId(Long ticketId);
    void deleteComment(Long commentId);
}