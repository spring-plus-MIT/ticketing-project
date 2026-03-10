package com.example.ticketingproject.domain.reservation.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {
    private final int httpStatus;
    private final ErrorStatus errorStatus;

    public ReservationException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.httpStatus = errorStatus.getHttpStatus().value(); // HttpStatus 객체에서 int 값 추출
        this.errorStatus = errorStatus;
    }
}