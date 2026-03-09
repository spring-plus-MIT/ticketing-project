package com.example.ticketingproject.auth.dto;

import lombok.Getter;

@Getter
public class RegisterResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String phone;
    private final String role;
    private final String status;

    public RegisterResponse(Long id, String email, String name, String phone, String role, String status) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }
}
