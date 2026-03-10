package com.example.ticketingproject.domain.castmember.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CastMemberResponse {
    private final Long id;
    private final String name;
    private final String roleName;
}