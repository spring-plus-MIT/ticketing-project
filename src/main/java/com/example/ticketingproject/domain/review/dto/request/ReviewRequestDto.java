package com.example.ticketingproject.domain.review.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Getter
@NoArgsConstructor
public class ReviewRequestDto {

    @Length(min = 1, max = 200, message = "리뷰 내용은 최소 1글자 부터 최대 200자 까지만 입력 가능합니다.")
    private String content;

    @Min(value = 1, message = "평점은 최소 1점부터 입력 가능합니다.")
    @Max(value = 5, message = "평점은 최대 5점까지 입력 가능합니다.")
    private int rating;

}
