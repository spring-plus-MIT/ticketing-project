package com.example.ticketingproject.domain.castmember.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CastMemberResponse {
    private Long id;
    private String name;
    private String roleName;
}