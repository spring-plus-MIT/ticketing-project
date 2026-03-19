package com.example.ticketingproject.domain.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_BLANK_ERROR;
import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;


@Getter
@NoArgsConstructor
public class ReviewRequestDto {

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 200, message = "리뷰 내용은 1자 이상 200자 이하로 입력해주세요")
    private String content;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Min(value = 1, message = "평점은 1점 이상으로 입력해주세요")
    @Max(value = 5, message = "평점은 5점 이하로 입력해주세요")
    private int rating;

}
