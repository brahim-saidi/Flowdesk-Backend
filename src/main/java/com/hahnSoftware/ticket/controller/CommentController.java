package com.hahnSoftware.ticket.controller;

import com.hahnSoftware.ticket.entity.Comment;
import com.hahnSoftware.ticket.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comment Management", description = "Operations pertaining to comments in the Ticket System")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "Add a new comment",
            description = "Creates a new comment for a specific ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created successfully",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Not authorized to add comment")
    })
    @PostMapping
    public ResponseEntity<Comment> addComment(
            @Parameter(description = "Comment object to be created", required = true)
            @RequestBody Comment comment) {
        
        Comment savedComment = commentService.addComment(comment);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    @Operation(summary = "Get comments by ticket ID",
            description = "Retrieves all comments associated with a specific ticket")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments",
                    content = @Content(schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Ticket not found"),
            @ApiResponse(responseCode = "403", description = "Not authorized to view comments")
    })
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<Comment>> getCommentsByTicketId(
            @Parameter(description = "ID of the ticket to get comments for", required = true)
            @PathVariable Long ticketId) {
        List<Comment> comments = commentService.getCommentsByTicketId(ticketId);
        return ResponseEntity.ok(comments);
    }


    
}