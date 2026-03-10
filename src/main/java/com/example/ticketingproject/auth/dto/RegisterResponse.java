package com.example.ticketingproject.auth.dto;

import com.example.ticketingproject.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String phone;
    private final String role;
    private final String status;

    public static RegisterResponse from(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getUserRole().getRoleName())
                .status(user.getUserStatus().getStatusName())
                .build();
    }

}
