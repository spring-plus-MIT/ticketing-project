package com.example.ticketingproject.auth.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_EMAIL_ERROR;

@Getter
public class LoginRequest {

    @Email(message = MSG_VALIDATION_EMAIL_ERROR)
    private String email;

    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

}
