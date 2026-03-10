package com.example.ticketingproject.domain.reservation.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;

public class ReservationException extends BaseException {

    public ReservationException(ErrorStatus errorStatus) {
        super(errorStatus.getHttpStatus(), errorStatus);
    }
}
