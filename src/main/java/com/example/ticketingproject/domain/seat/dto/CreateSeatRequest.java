package com.example.ticketingproject.domain.seat.dto;

import com.example.ticketingproject.common.enums.GradeName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_LENGTH_ERROR;
import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

@Getter
public class CreateSeatRequest {

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Enumerated(EnumType.STRING)
    private GradeName gradeName;

    @NotBlank
    @Length(max = 10, message = "좌석 열 이름은 1자 이상 10 이하로 입력해주세요")
    private String rowName;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Min(value = 1, message = "좌석 번호는 1번 이상으로 입력해주세요")
    @Max(value = 10, message = "좌석 번호는 10번 이하로 입력해주세요")
    private int seatNumber;
}
