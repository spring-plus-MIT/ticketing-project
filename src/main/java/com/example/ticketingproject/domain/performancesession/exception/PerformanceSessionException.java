package com.example.ticketingproject.domain.performancesession.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PerformanceSessionException extends BaseException {
    public PerformanceSessionException(HttpStatus status, ErrorStatus errorStatusCode)  {
        super(status, errorStatusCode);
    }
}