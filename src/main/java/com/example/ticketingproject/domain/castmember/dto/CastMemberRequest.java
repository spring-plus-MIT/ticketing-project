package com.example.ticketingproject.domain.castmember.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_BLANK_ERROR;

@Getter
public class CastMemberRequest {

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 50, message = "출연자명은 1자 이상 50자 이하로 입력해주세요")
    private String name;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 50, message = "배역은 1자 이상 50자 이하로 입력해주세요")
    private String roleName;
}