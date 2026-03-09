package com.example.ticketingproject.security.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CustomJwtException extends BaseException {

    public CustomJwtException(HttpStatus status, ErrorStatus errorStatusCode) {
        super(status, errorStatusCode);
    }
}
