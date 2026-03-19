package com.example.ticketingproject.domain.work.dto;

import com.example.ticketingproject.domain.work.enums.Category;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_BLANK_ERROR;
import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

@Getter
public class UpdateWorkRequest {

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 100, message = "제목은 1자 이상 100자 이하로 입력해주세요")
    private String title;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Enumerated(EnumType.STRING)
    private Category category;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 255, message = "설명은 1자 이상 255자 이하로 입력해주세요")
    private String description;
}
