package com.example.ticketingproject.domain.user.dto;

import com.example.ticketingproject.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserResponse {
    private final Long id;
    private final String name;
    private final String phone;

    public static UpdateUserResponse from(User user) {
        return UpdateUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }
}
