package com.example.ticketingproject.domain.seat.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SeatException extends BaseException {
    public SeatException(HttpStatus status, ErrorStatus errorStatusCode) {
        super(status, errorStatusCode);
    }
}
