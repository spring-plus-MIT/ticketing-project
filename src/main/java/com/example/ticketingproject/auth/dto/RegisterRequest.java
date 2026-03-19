package com.example.ticketingproject.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
public class RegisterRequest {

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Email(message = MSG_VALIDATION_EMAIL_ERROR)
    @Length(max = 50, message = "이메일의 길이는 50이하로 입력해주세요")
    private String email;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 30, message = "이름은 1글자 이상 30자 이하로 입력해주세요")
    private String name;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요")
    private String password;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = MSG_VALIDATION_PATTERN_ERROR
    )
    private String phone;
}
