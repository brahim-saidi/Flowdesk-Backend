package com.hahnSoftware.ticket.entity;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO{


    private Long commentId;
    private String content;
    private Timestamp createdAt;
    private UserSummaryDTO user;

}
