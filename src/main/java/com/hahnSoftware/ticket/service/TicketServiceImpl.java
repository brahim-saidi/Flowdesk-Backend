package com.hahnSoftware.ticket.service;

import com.hahnSoftware.ticket.entity.Comment;
import com.hahnSoftware.ticket.entity.CommentDTO;
import com.hahnSoftware.ticket.entity.Ticket;
import com.hahnSoftware.ticket.entity.TicketDTO;
import com.hahnSoftware.ticket.entity.UserSummaryDTO;
import com.hahnSoftware.ticket.entity.Users;
import com.hahnSoftware.ticket.repository.TicketRepository;
import com.hahnSoftware.ticket.repository.UserRepository;
import com.hahnSoftware.ticket.security.CurrentUserService;

import java.time.Instant;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.hahnSoftware.ticket.repository.TicketSpecs;

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
    private final CurrentUserService currentUserService;
    private final TicketAccessService ticketAccessService;

    public TicketServiceImpl(
            TicketRepository ticketRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService,
            TicketAccessService ticketAccessService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.ticketAccessService = ticketAccessService;
    }
   
   
    @Override
    public Ticket createTicket(Ticket ticket) {
        if (ticket.getCreatedBy() == null || ticket.getCreatedBy().getUserId() == null) {
            throw new IllegalArgumentException("Created by user is required.");
        }

        Users user = userRepository.findById(ticket.getCreatedBy().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + ticket.getCreatedBy().getUserId()));

        if (!user.isEnabled()) {
            throw new AccessDeniedException("Account is disabled");
        }

        ticket.setCreatedBy(user);

        return ticketRepository.save(ticket);
    }
    @Override
    public Ticket updateTicketStatus(Long ticketId, Ticket.Status status) {
        Ticket ticket = ticketAccessService.requireAccessibleTicket(ticketId);
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

    @Transactional
    @Override
    public List<TicketDTO> getAllTickets() {
        List<Ticket> tickets = currentUserService.isItSupport()
                ? ticketRepository.findAll()
                : ticketRepository.findVisibleToUser(currentUserService.requireUserId());
        return tickets.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Page<TicketDTO> searchTickets(
            String title,
            Instant createdFrom,
            Instant createdTo,
            Long assigneeUserId,
            Pageable pageable) {
        Specification<Ticket> spec = Specification.where(TicketSpecs.titleContains(title))
                .and(TicketSpecs.createdAtFrom(createdFrom))
                .and(TicketSpecs.createdAtTo(createdTo))
                .and(TicketSpecs.assigneeUserId(assigneeUserId));
        if (!currentUserService.isItSupport()) {
            spec = spec.and(TicketSpecs.visibleToEmployee(currentUserService.requireUserId()));
        }
        return ticketRepository.findAll(spec, pageable).map(this::convertToDTO);
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
        return ticketAccessService.requireAccessibleTicket(ticketId);
    }

    @Override
    public List<Ticket> getTicketsByStatus(Ticket.Status status) {
        if (currentUserService.isItSupport()) {
            return ticketRepository.findByStatus(status);
        }
        return ticketRepository.findByStatusVisibleToUser(status, currentUserService.requireUserId());
    }



     public TicketDTO getTicketDTOById(Long ticketId) {
        Ticket ticket = ticketAccessService.requireAccessibleTicket(ticketId);
            
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