package com.example.ticketingproject.domain.work.dto;

import com.example.ticketingproject.domain.work.enums.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_LENGTH_ERROR;
import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_BLANK_ERROR;

@Getter
public class UpdateWorkRequest {
    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 30, message = MSG_VALIDATION_LENGTH_ERROR)
    private String title;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    private Category category;

    private String description;
}
