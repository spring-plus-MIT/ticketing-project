package com.example.ticketingproject.domain.user.dto;

import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
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

    public GetUserResponse(Long id, String name, String email, String phone, BigDecimal balance, UserRole userRole, UserStatus userStatus, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.balance = balance;
        this.userRole = userRole;
        this.userStatus = userStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
