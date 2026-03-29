package com.hahnSoftware.ticket.service;

import com.hahnSoftware.ticket.entity.Comment;
import com.hahnSoftware.ticket.entity.CommentDTO;
import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.entity.TicketDTO;
import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.repository.TicketRepository;
import com.hahnSoftware.ticket.repository.UserRepository;
import com.hahnSoftware.ticket.security.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private TicketAccessService ticketAccessService;

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
        createdByUser.setEnabled(true);

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

        lenient().when(currentUserService.isItSupport()).thenReturn(true);
        lenient().when(currentUserService.requireUserId()).thenReturn(1L);
        lenient().when(ticketAccessService.requireAccessibleTicket(ArgumentMatchers.anyLong())).thenAnswer(invocation -> {
            long id = invocation.getArgument(0, Long.class);
            if (id == 1L) {
                return sampleTicket;
            }
            if (id == 2L) {
                return anotherTicket;
            }
            throw new EntityNotFoundException("Ticket not found with id: " + id);
        });
    }

    @Test
    void createTicket_Success() {
        // Mock UserRepository behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(createdByUser));

        // Mock TicketRepository behavior
        when(ticketRepository.save(ArgumentMatchers.any(Ticket.class))).thenReturn(sampleTicket);

        // Act
        Ticket createdTicket = ticketService.createTicket(sampleTicket);

        // Assert
        Assertions.assertNotNull(createdTicket);
        Assertions.assertEquals(sampleTicket.getTitle(), createdTicket.getTitle());
        Assertions.assertEquals(sampleTicket.getDescription(), createdTicket.getDescription());
        Assertions.assertEquals(sampleTicket.getPriority(), createdTicket.getPriority());
        Assertions.assertEquals(sampleTicket.getCategory(), createdTicket.getCategory());
        Assertions.assertEquals(sampleTicket.getStatus(), createdTicket.getStatus());
        Assertions.assertNotNull(createdTicket.getCreatedBy());
        Assertions.assertEquals(createdByUser.getUserId(), createdTicket.getCreatedBy().getUserId());
        
        // Verify repository interactions
        verify(userRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(ArgumentMatchers.any(Ticket.class));
    }
    
    @Test
    void createTicket_MissingCreatedByUser() {
        // Prepare test data
        Ticket ticketWithoutUser = new Ticket();
        ticketWithoutUser.setTitle("Invalid Ticket");
        
        // Act & Assert
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(ticketWithoutUser);
        });
        
        Assertions.assertTrue(exception.getMessage().contains("Created by user is required"));
        verify(ticketRepository, never()).save(ArgumentMatchers.any(Ticket.class));
    }
    
    @Test
    void createTicket_DisabledCreator() {
        createdByUser.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(createdByUser));

        Assertions.assertThrows(AccessDeniedException.class, () -> ticketService.createTicket(sampleTicket));

        verify(ticketRepository, never()).save(ArgumentMatchers.any(Ticket.class));
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
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(ticketWithInvalidUser);
        });
        
        Assertions.assertTrue(exception.getMessage().contains("User not found with ID: 999"));
        verify(userRepository, times(1)).findById(999L);
        verify(ticketRepository, never()).save(ArgumentMatchers.any(Ticket.class));
    }
    
    @Test
    void updateTicketStatus_Success() {
        when(ticketRepository.save(ArgumentMatchers.any(Ticket.class))).thenReturn(sampleTicket);

        Ticket updatedTicket = ticketService.updateTicketStatus(1L, Ticket.Status.IN_PROGRESS);

        Assertions.assertNotNull(updatedTicket);
        Assertions.assertEquals(Ticket.Status.IN_PROGRESS, updatedTicket.getStatus());

        verify(ticketAccessService, times(1)).requireAccessibleTicket(1L);
        verify(ticketRepository, times(1)).save(ArgumentMatchers.any(Ticket.class));
    }
    
    @Test
    void updateTicketStatus_TicketNotFound() {
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            ticketService.updateTicketStatus(999L, Ticket.Status.IN_PROGRESS);
        });

        Assertions.assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketAccessService, times(1)).requireAccessibleTicket(999L);
        verify(ticketRepository, never()).save(ArgumentMatchers.any(Ticket.class));
    }
    
    @Test
    void getAllTickets_Success() {
        // Mock TicketRepository behavior
        when(ticketRepository.findAll()).thenReturn(ticketList);
        
        // Act
        List<TicketDTO> ticketDTOs = ticketService.getAllTickets();
        
        // Assert
        Assertions.assertNotNull(ticketDTOs);
        Assertions.assertEquals(2, ticketDTOs.size());
        
        // Check first DTO
        TicketDTO firstDTO = ticketDTOs.get(0);
        Assertions.assertEquals(sampleTicket.getTicketId(), firstDTO.getTicketId());
        Assertions.assertEquals(sampleTicket.getTitle(), firstDTO.getTitle());
        Assertions.assertEquals(sampleTicket.getStatus(), firstDTO.getStatus());
        
        // Check second DTO
        TicketDTO secondDTO = ticketDTOs.get(1);
        Assertions.assertEquals(anotherTicket.getTicketId(), secondDTO.getTicketId());
        Assertions.assertEquals(anotherTicket.getTitle(), secondDTO.getTitle());
        Assertions.assertEquals(anotherTicket.getStatus(), secondDTO.getStatus());
        
        // Verify repository interactions
        verify(ticketRepository, times(1)).findAll();
    }
    
    @Test
    void getTicketById_Success() {
        Ticket foundTicket = ticketService.getTicketById(1L);

        Assertions.assertNotNull(foundTicket);
        Assertions.assertEquals(sampleTicket.getTicketId(), foundTicket.getTicketId());
        Assertions.assertEquals(sampleTicket.getTitle(), foundTicket.getTitle());

        verify(ticketAccessService, times(1)).requireAccessibleTicket(1L);
    }

    @Test
    void getTicketById_TicketNotFound() {
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            ticketService.getTicketById(999L);
        });

        Assertions.assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketAccessService, times(1)).requireAccessibleTicket(999L);
    }
    
    @Test
    void getTicketsByStatus_Success() {
        // Mock TicketRepository behavior
        when(ticketRepository.findByStatus(Ticket.Status.NEW)).thenReturn(List.of(sampleTicket));
        
        // Act
        List<Ticket> foundTickets = ticketService.getTicketsByStatus(Ticket.Status.NEW);
        
        // Assert
        Assertions.assertNotNull(foundTickets);
        Assertions.assertEquals(1, foundTickets.size());
        Assertions.assertEquals(sampleTicket.getTicketId(), foundTickets.get(0).getTicketId());
        Assertions.assertEquals(Ticket.Status.NEW, foundTickets.get(0).getStatus());
        
        // Verify repository interactions
        verify(ticketRepository, times(1)).findByStatus(Ticket.Status.NEW);
    }
    
    @Test
    void getTicketDTOById_Success() {
        TicketDTO ticketDTO = ticketService.getTicketDTOById(1L);
        
        // Assert
        Assertions.assertNotNull(ticketDTO);
        Assertions.assertEquals(sampleTicket.getTicketId(), ticketDTO.getTicketId());
        Assertions.assertEquals(sampleTicket.getTitle(), ticketDTO.getTitle());
        Assertions.assertEquals(sampleTicket.getDescription(), ticketDTO.getDescription());
        Assertions.assertEquals(sampleTicket.getPriority(), ticketDTO.getPriority());
        Assertions.assertEquals(sampleTicket.getCategory(), ticketDTO.getCategory());
        Assertions.assertEquals(sampleTicket.getStatus(), ticketDTO.getStatus());
        
        // Check assigned user
        Assertions.assertNotNull(ticketDTO.getAssignedUser());
        Assertions.assertEquals(assignedUser.getUserId(), ticketDTO.getAssignedUser().getUserId());
        Assertions.assertEquals(assignedUser.getUsername(), ticketDTO.getAssignedUser().getUsername());
        
        // Check created by user
        Assertions.assertNotNull(ticketDTO.getCreatedBy());
        Assertions.assertEquals(createdByUser.getUserId(), ticketDTO.getCreatedBy().getUserId());
        Assertions.assertEquals(createdByUser.getUsername(), ticketDTO.getCreatedBy().getUsername());
        
        // Check comments
        Assertions.assertNotNull(ticketDTO.getComments());
        Assertions.assertEquals(2, ticketDTO.getComments().size());
        
        CommentDTO firstComment = ticketDTO.getComments().get(0);
        Assertions.assertEquals(comment1.getCommentId(), firstComment.getCommentId());
        Assertions.assertEquals(comment1.getContent(), firstComment.getContent());
        Assertions.assertEquals(createdByUser.getUserId(), firstComment.getUser().getUserId());
        
        verify(ticketAccessService, times(1)).requireAccessibleTicket(1L);
    }

    @Test
    void getTicketDTOById_TicketNotFound() {
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            ticketService.getTicketDTOById(999L);
        });

        Assertions.assertTrue(exception.getMessage().contains("Ticket not found"));
        verify(ticketAccessService, times(1)).requireAccessibleTicket(999L);
    }
}