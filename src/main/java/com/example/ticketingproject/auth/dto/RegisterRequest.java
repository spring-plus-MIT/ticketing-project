package com.example.ticketingproject.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_EMAIL_ERROR;
import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_PATTERN_ERROR;

@Getter
public class RegisterRequest {
    @Email(message = MSG_VALIDATION_EMAIL_ERROR)
    private String email;

    @Length(min = 1, max = 30, message = "이름은 1글자 이상 30자 이하로 입력해주세요.")
    private String name;

    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = MSG_VALIDATION_PATTERN_ERROR
    )
    private String phone;
}
