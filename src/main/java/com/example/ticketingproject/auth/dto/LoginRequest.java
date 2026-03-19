package com.example.ticketingproject.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
public class LoginRequest {

    @Email(message = MSG_VALIDATION_EMAIL_ERROR)
    private String email;

    @Length(min = 8, max = 20, message = MSG_VALIDATION_LENGTH_ERROR)
    private String password;

}
