package com.example.ticketingproject.domain.user.dto;

import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class GetUserResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final String phone;
    private final BigDecimal balance;
    private final UserRole userRole;
    private final UserStatus userStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
}
