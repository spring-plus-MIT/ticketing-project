package com.example.ticketingproject.domain.work.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class WorkException extends BaseException {
    public WorkException(HttpStatus status, ErrorStatus errorStatusCode)  {
        super(status, errorStatusCode);
    }
}
