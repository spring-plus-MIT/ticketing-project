package com.example.ticketingproject.domain.work.dto;

import com.example.ticketingproject.domain.work.enums.Category;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

@Getter
public class UpdateWorkRequest {

    @Length(min = 1, max = 100, message = "제목은 1자 이상 100자 이하로 입력해주세요.")
    private String title;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    private Category category;

    @Length(min = 1, max = 255, message = "설명은 1자 이상 255자 이하로 입력해주세요.")
    private String description;
}
