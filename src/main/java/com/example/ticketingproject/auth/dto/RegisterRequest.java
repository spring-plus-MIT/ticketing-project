package com.example.ticketingproject.auth.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
public class RegisterRequest {
    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Email(message = MSG_VALIDATION_EMAIL_ERROR)
    private String email;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    private String name;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(min = 8, max = 20, message = MSG_VALIDATION_LENGTH_ERROR)
    private String password;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 14, message = MSG_VALIDATION_LENGTH_ERROR)
    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = MSG_VALIDATION_PATTERN_ERROR
    )
    private String phone;
}
