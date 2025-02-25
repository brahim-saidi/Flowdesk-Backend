package com.hahnSoftware.ticket.service;

import com.hahnSoftware.ticket.entity.*;
import com.hahnSoftware.ticket.repository.TicketRepository;
import com.hahnSoftware.ticket.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket sampleTicket;
    private Ticket anotherTicket;
    private Users createdByUser;
    private Users assignedUser;
    private Comment comment1;
    private Comment comment2;
    private List<Ticket> ticketList;

    @BeforeEach
    void setUp() {
        // Initialize test users
        createdByUser = new Users();
        createdByUser.setUserId(1L);
        createdByUser.setUsername("testUser");
        createdByUser.setEmail("testuser@example.com");
        createdByUser.setRole(Users.Role.EMPLOYEE);

        assignedUser = new Users();
        assignedUser.setUserId(2L);
        assignedUser.setUsername("supportUser");
        assignedUser.setEmail("support@example.com");
        assignedUser.setRole(Users.Role.IT_SUPPORT);

        // Initialize comments
        comment1 = new Comment();
        comment1.setCommentId(1L);
        comment1.setContent("Test comment 1");
        comment1.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        comment1.setUser(createdByUser);

        comment2 = new Comment();
        comment2.setCommentId(2L);
        comment2.setContent("Test comment 2");
        comment2.setCreatedAt(new Timestamp(System.currentTimeMillis() + 3600000)); // 1 hour later
        comment2.setUser(assignedUser);

        // Initialize test tickets
        sampleTicket = new Ticket();
        sampleTicket.setTicketId(1L);
        sampleTicket.setTitle("Test Ticket");
        sampleTicket.setDescription("Test Description");
        sampleTicket.setPriority(Ticket.Priority.HIGH);
        sampleTicket.setCategory(Ticket.Category.SOFTWARE);
        sampleTicket.setStatus(Ticket.Status.NEW);
        sampleTicket.setCreatedBy(createdByUser);
        sampleTicket.setAssignedUser(assignedUser);
        sampleTicket.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        sampleTicket.setComments(comments);

        anotherTicket = new Ticket();
        anotherTicket.setTicketId(2L);
        anotherTicket.setTitle("Another Ticket");
        anotherTicket.setDescription("Another Description");
        anotherTicket.setPriority(Ticket.Priority.MEDIUM);
        anotherTicket.setCategory(Ticket.Category.HARDWARE);
        anotherTicket.setStatus(Ticket.Status.IN_PROGRESS);
        anotherTicket.setCreatedBy(createdByUser);
        anotherTicket.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Create a list of tickets for findAll
        ticketList = Arrays.asList(sampleTicket, anotherTicket);
    }

    @Test
    void createTicket_Success() {
        // Mock UserRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(createdByUser));

        // Mock TicketRepository behavior
        when(ticketRepository.save(any(Ticket.class))).thenReturn(sampleTicket);

        // Act
        Ticket createdTicket = ticketService.createTicket(sampleTicket);

        // Assert
        assertNotNull(createdTicket);
        assertEquals(sampleTicket.getTitle(), createdTicket.getTitle());
        assertEquals(sampleTicket.getDescription(), createdTicket.getDescription());
        assertEquals(sampleTicket.getPriority(), createdTicket.getPriority());
        assertEquals(sampleTicket.getCategory(), createdTicket.getCategory());
        assertEquals(sampleTicket.getStatus(), createdTicket.getStatus());
        assertNotNull(createdTicket.getCreatedBy());
        assertEquals(createdByUser.getUserId(), createdTicket.getCreatedBy().getUserId());
        
        // Verify repository interactions
        verify(userRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
    
    @Test
    void createTicket_MissingCreatedByUser() {
        // Prepare test data
        Ticket ticketWithoutUser = new Ticket();
        ticketWithoutUser.setTitle("Invalid Ticket");
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(ticketWithoutUser);
        });
        
        assertTrue(exception.getMessage().contains("Created by user is required"));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
    
    @Test
    void createTicket_NonExistentUser() {
        // Prepare test data
        Users nonExistentUser = new Users();
        nonExistentUser.setUserId(999L);
        
        Ticket ticketWithInvalidUser = new Ticket();
        ticketWithInvalidUser.setTitle("Invalid Ticket");
        ticketWithInvalidUser.setCreatedBy(nonExistentUser);
        
        // Mock UserRepository behavior
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(ticketWithInvalidUser);
        });
        
        assertTrue(exception.getMessage().contains("User not found with ID: 999"));
        verify(userRepository, times(1)).findById(999L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
    
    @Test
    void updateTicketStatus_Success() {
        // Mock TicketRepository behavior
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(sampleTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(sampleTicket);
        
        // Act
        Ticket updatedTicket = ticketService.updateTicketStatus(1L, Ticket.Status.IN_PROGRESS);
        
        // Assert
        assertNotNull(updatedTicket);
        assertEquals(Ticket.Status.IN_PROGRESS, updatedTicket.getStatus());
        
        // Verify repository interactions
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }
    
    @Test
    void updateTicketStatus_TicketNotFound() {
        // Mock TicketRepository behavior
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            ticketService.updateTicketStatus(999L, Ticket.Status.IN_PROGRESS);
        });
        
        assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketRepository, times(1)).findById(999L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
    
    @Test
    void getAllTickets_Success() {
        // Mock TicketRepository behavior
        when(ticketRepository.findAll()).thenReturn(ticketList);
        
        // Act
        List<TicketDTO> ticketDTOs = ticketService.getAllTickets();
        
        // Assert
        assertNotNull(ticketDTOs);
        assertEquals(2, ticketDTOs.size());
        
        // Check first DTO
        TicketDTO firstDTO = ticketDTOs.get(0);
        assertEquals(sampleTicket.getTicketId(), firstDTO.getTicketId());
        assertEquals(sampleTicket.getTitle(), firstDTO.getTitle());
        assertEquals(sampleTicket.getStatus(), firstDTO.getStatus());
        
        // Check second DTO
        TicketDTO secondDTO = ticketDTOs.get(1);
        assertEquals(anotherTicket.getTicketId(), secondDTO.getTicketId());
        assertEquals(anotherTicket.getTitle(), secondDTO.getTitle());
        assertEquals(anotherTicket.getStatus(), secondDTO.getStatus());
        
        // Verify repository interactions
        verify(ticketRepository, times(1)).findAll();
    }
    
    @Test
    void getTicketById_Success() {
        // Mock TicketRepository behavior
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(sampleTicket));
        
        // Act
        Ticket foundTicket = ticketService.getTicketById(1L);
        
        // Assert
        assertNotNull(foundTicket);
        assertEquals(sampleTicket.getTicketId(), foundTicket.getTicketId());
        assertEquals(sampleTicket.getTitle(), foundTicket.getTitle());
        
        // Verify repository interactions
        verify(ticketRepository, times(1)).findById(1L);
    }
    
    @Test
    void getTicketById_TicketNotFound() {
        // Mock TicketRepository behavior
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            ticketService.getTicketById(999L);
        });
        
        assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketRepository, times(1)).findById(999L);
    }
    
    @Test
    void getTicketsByStatus_Success() {
        // Mock TicketRepository behavior
        when(ticketRepository.findByStatus(Ticket.Status.NEW)).thenReturn(List.of(sampleTicket));
        
        // Act
        List<Ticket> foundTickets = ticketService.getTicketsByStatus(Ticket.Status.NEW);
        
        // Assert
        assertNotNull(foundTickets);
        assertEquals(1, foundTickets.size());
        assertEquals(sampleTicket.getTicketId(), foundTickets.get(0).getTicketId());
        assertEquals(Ticket.Status.NEW, foundTickets.get(0).getStatus());
        
        // Verify repository interactions
        verify(ticketRepository, times(1)).findByStatus(Ticket.Status.NEW);
    }
    
    @Test
    void getTicketDTOById_Success() {
        // Mock TicketRepository behavior
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(sampleTicket));
        
        // Act
        TicketDTO ticketDTO = ticketService.getTicketDTOById(1L);
        
        // Assert
        assertNotNull(ticketDTO);
        assertEquals(sampleTicket.getTicketId(), ticketDTO.getTicketId());
        assertEquals(sampleTicket.getTitle(), ticketDTO.getTitle());
        assertEquals(sampleTicket.getDescription(), ticketDTO.getDescription());
        assertEquals(sampleTicket.getPriority(), ticketDTO.getPriority());
        assertEquals(sampleTicket.getCategory(), ticketDTO.getCategory());
        assertEquals(sampleTicket.getStatus(), ticketDTO.getStatus());
        
        // Check assigned user
        assertNotNull(ticketDTO.getAssignedUser());
        assertEquals(assignedUser.getUserId(), ticketDTO.getAssignedUser().getUserId());
        assertEquals(assignedUser.getUsername(), ticketDTO.getAssignedUser().getUsername());
        
        // Check created by user
        assertNotNull(ticketDTO.getCreatedBy());
        assertEquals(createdByUser.getUserId(), ticketDTO.getCreatedBy().getUserId());
        assertEquals(createdByUser.getUsername(), ticketDTO.getCreatedBy().getUsername());
        
        // Check comments
        assertNotNull(ticketDTO.getComments());
        assertEquals(2, ticketDTO.getComments().size());
        
        CommentDTO firstComment = ticketDTO.getComments().get(0);
        assertEquals(comment1.getCommentId(), firstComment.getCommentId());
        assertEquals(comment1.getContent(), firstComment.getContent());
        assertEquals(createdByUser.getUserId(), firstComment.getUser().getUserId());
        
        // Verify repository interactions
        verify(ticketRepository, times(1)).findById(1L);
    }
    
    @Test
    void getTicketDTOById_TicketNotFound() {
        // Mock TicketRepository behavior
        when(ticketRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            ticketService.getTicketDTOById(999L);
        });
        
        assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketRepository, times(1)).findById(999L);
    }
}