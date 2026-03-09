package com.example.ticketingproject.domain.castmember.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CastMemberRequest {
    private String name;
    private String roleName;
}