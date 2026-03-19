package com.example.ticketingproject.domain.venue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_BLANK_ERROR;

@Getter
public class UpdateVenueRequest {

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 100, message = "이름은 1자 이상 100자 이하로 입력해주세요")
    private String name;

    @NotBlank(message = MSG_VALIDATION_NOT_BLANK_ERROR)
    @Length(max = 255, message = "주소는 1자 이상 255자 이하로 입력해주세요")
    private String address;

    @Min(value = 1, message = "총 좌석 수는 1 이상으로 입력해주세요")
    private int totalSeats;
}
