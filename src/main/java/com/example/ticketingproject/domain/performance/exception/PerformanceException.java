package com.example.ticketingproject.domain.performance.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PerformanceException extends BaseException {
    public PerformanceException(HttpStatus status, ErrorStatus errorStatusCode)  {
        super(status, errorStatusCode);
    }
}
