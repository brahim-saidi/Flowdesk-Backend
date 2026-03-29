package com.hahnSoftware.ticket.service;

import com.hahnSoftware.ticket.entity.Comment;
import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TicketAccessService ticketAccessService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment testComment;
    private Users testUser;
    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        // Initialize test user
        testUser = new Users();
        testUser.setUserId(1L);
        testUser.setUsername("testUser");

        // Initialize test ticket
        testTicket = new Ticket();
        testTicket.setTicketId(1L);
        testTicket.setTitle("Test Ticket");

        // Initialize test comment
        testComment = new Comment();
        testComment.setCommentId(1L);
        testComment.setContent("Test comment content");
        testComment.setCreatedAt(Timestamp.from(Instant.now()));
        testComment.setUser(testUser);
        testComment.setTicket(testTicket);

        lenient().when(ticketAccessService.requireAccessibleTicket(anyLong())).thenAnswer(invocation -> {
            Ticket t = new Ticket();
            t.setTicketId(invocation.getArgument(0, Long.class));
            return t;
        });
    }

    @Test
    void addComment_ShouldSaveAndReturnComment() {
        // Arrange
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // Act
        Comment savedComment = commentService.addComment(testComment);

        // Assert
        assertNotNull(savedComment);
        assertEquals(testComment.getContent(), savedComment.getContent());
        assertEquals(testComment.getCommentId(), savedComment.getCommentId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void getCommentsByTicketId_ShouldReturnListOfComments() {
        // Arrange
        List<Comment> expectedComments = Arrays.asList(testComment);
        when(commentRepository.findByTicket_TicketId(anyLong())).thenReturn(expectedComments);

        // Act
        List<Comment> actualComments = commentService.getCommentsByTicketId(1L);

        // Assert
        assertNotNull(actualComments);
        assertFalse(actualComments.isEmpty());
        assertEquals(1, actualComments.size());
        assertEquals(testComment.getContent(), actualComments.get(0).getContent());
        verify(ticketAccessService, times(1)).requireAccessibleTicket(1L);
        verify(commentRepository, times(1)).findByTicket_TicketId(1L);
    }

    @Test
    void deleteComment_ShouldCallRepositoryDelete() {
        // Arrange
        Long commentId = 1L;
        doNothing().when(commentRepository).deleteById(anyLong());

        // Act
        commentService.deleteComment(commentId);

        // Assert
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void getCommentsByTicketId_ShouldReturnEmptyList_WhenNoCommentsExist() {
        // Arrange
        when(commentRepository.findByTicket_TicketId(anyLong())).thenReturn(Arrays.asList());

        // Act
        List<Comment> comments = commentService.getCommentsByTicketId(999L);

        // Assert
        assertNotNull(comments);
        assertTrue(comments.isEmpty());
        verify(ticketAccessService, times(1)).requireAccessibleTicket(999L);
        verify(commentRepository, times(1)).findByTicket_TicketId(999L);
    }

    @Test
    void addComment_ShouldHandleNullContent() {
        // Arrange
        Comment commentWithNullContent = new Comment();
        commentWithNullContent.setTicket(testTicket);
        commentWithNullContent.setUser(testUser);

        when(commentRepository.save(any(Comment.class))).thenReturn(commentWithNullContent);

        // Act & Assert
        assertDoesNotThrow(() -> commentService.addComment(commentWithNullContent));
        verify(ticketAccessService, times(1)).requireAccessibleTicket(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}