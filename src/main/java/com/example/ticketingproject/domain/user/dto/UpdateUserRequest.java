package com.example.ticketingproject.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class UpdateUserRequest {
    @Length(min = 1, max = 10, message = "이름은 1자 이상 10자 이하로 입력해주세요.")
    private String name;

    @Length(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "전화번호는 010-XXXX-XXXX 형식으로 입력해주세요."
    )
    private String phone;
}
