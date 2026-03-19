package com.example.ticketingproject.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_EMAIL_ERROR;
import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_BLANK_ERROR;

@Getter
public class LoginRequest {

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Email(message = MSG_VALIDATION_EMAIL_ERROR)
    private String email;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

}
