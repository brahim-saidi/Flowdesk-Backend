package com.hahnSoftware.ticket.entity;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor


@Getter
@Setter
public class UserSummaryDTO {

    private Long userId;
    private String username;
    private String email;
    private String role;

}



