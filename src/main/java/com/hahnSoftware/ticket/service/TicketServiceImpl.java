package com.hahnSoftware.ticket.service;

import com.hahnSoftware.ticket.entity.Comment;
import com.hahnSoftware.ticket.entity.CommentDTO;
import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.entity.TicketDTO;
import com.hahnSoftware.ticket.entity.UserSummaryDTO;
import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.repository.TicketRepository;
import com.hahnSoftware.ticket.repository.UserRepository;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Primary
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

   
    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }
   
   
    @Override
    public Ticket createTicket(Ticket ticket) {
        if (ticket.getCreatedBy() == null || ticket.getCreatedBy().getUserId() == null) {
            throw new IllegalArgumentException("Created by user is required.");
        }

        Users user = userRepository.findById(ticket.getCreatedBy().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + ticket.getCreatedBy().getUserId()));

        ticket.setCreatedBy(user);

        return ticketRepository.save(ticket);
    }
    @Override
    public Ticket updateTicketStatus(Long ticketId, Ticket.Status status) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

    @Transactional
    @Override
    public List<TicketDTO> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }



    
    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setTicketId(ticket.getTicketId());
        ticketDTO.setTitle(ticket.getTitle());
        ticketDTO.setDescription(ticket.getDescription());
        ticketDTO.setPriority(ticket.getPriority());
        ticketDTO.setCategory(ticket.getCategory());
        ticketDTO.setStatus(ticket.getStatus());
        ticketDTO.setCreatedAt(ticket.getCreatedAt());
        
        Users assignedUser = ticket.getAssignedUser();
        if (assignedUser != null) {
            ticketDTO.setAssignedUser(convertToUserSummaryDTO(assignedUser));
        }
        
        Users createdBy = ticket.getCreatedBy();
        if (createdBy != null) {
            org.hibernate.Hibernate.initialize(createdBy);
            ticketDTO.setCreatedBy(convertToUserSummaryDTO(createdBy));
        }
        

        
        List<Comment> comments = ticket.getComments();
        if (comments != null) {
            org.hibernate.Hibernate.initialize(comments);
            List<CommentDTO> commentDTOs = comments.stream()
                .filter(comment -> comment.getUser() != null)
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList());
            ticketDTO.setComments(commentDTOs);
        }
        
        return ticketDTO;
    }
    
    private UserSummaryDTO convertToUserSummaryDTO(Users user) {
        return new UserSummaryDTO(
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().toString()
        );
    }
    
    private CommentDTO convertToCommentDTO(Comment comment) {
        Users commentUser = comment.getUser();
        return new CommentDTO(
            comment.getCommentId(),
            comment.getContent(),
            comment.getCreatedAt(),
            convertToUserSummaryDTO(commentUser)
        );
    }
    @Override
    public Ticket getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
            .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
    }

    @Override
    public List<Ticket> getTicketsByStatus(Ticket.Status status) {
        return ticketRepository.findByStatus(status);
    }



     public TicketDTO getTicketDTOById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new EntityNotFoundException("Ticket not found with id: " + ticketId));
            
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setTicketId(ticket.getTicketId());
        ticketDTO.setTitle(ticket.getTitle());
        ticketDTO.setDescription(ticket.getDescription());
        ticketDTO.setPriority(ticket.getPriority());
        ticketDTO.setCategory(ticket.getCategory());
        ticketDTO.setStatus(ticket.getStatus());
        ticketDTO.setCreatedAt(ticket.getCreatedAt());
        
        if (ticket.getAssignedUser() != null) {
            Users assignedUser = ticket.getAssignedUser();
            ticketDTO.setAssignedUser(new UserSummaryDTO(
                assignedUser.getUserId(),
                assignedUser.getUsername(),
                assignedUser.getEmail(),
                assignedUser.getRole().toString()
            ));
        }
        
        if (ticket.getCreatedBy() != null) {
            Users createdBy = ticket.getCreatedBy();
            ticketDTO.setCreatedBy(new UserSummaryDTO(
                createdBy.getUserId(),
                createdBy.getUsername(),
                createdBy.getEmail(),
                createdBy.getRole().toString()
            ));
        }
        
        if (ticket.getComments() != null) {
            List<CommentDTO> comments = new ArrayList<>();
            for (Comment comment : ticket.getComments()) {
                if (comment.getUser() != null) {
                    Users commentUser = comment.getUser();
                    comments.add(new CommentDTO(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        new UserSummaryDTO(
                            commentUser.getUserId(),
                            commentUser.getUsername(),
                            commentUser.getEmail(),
                            commentUser.getRole().toString()
                        )
                    ));
                }
            }
            ticketDTO.setComments(comments);
        }
        
        return ticketDTO;
    }
}