package com.example.ticketingproject.common.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ErrorStatus errorStatusCode;

    public BaseException(HttpStatus httpStatus, ErrorStatus errorStatusCode) {
        super(errorStatusCode.getMessage());
        this.httpStatus = httpStatus;
        this.errorStatusCode = errorStatusCode;
    }
}