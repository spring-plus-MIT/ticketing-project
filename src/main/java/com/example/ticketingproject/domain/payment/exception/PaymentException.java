package com.example.ticketingproject.domain.payment.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PaymentException extends BaseException {
    public PaymentException(HttpStatus status, ErrorStatus errorStatusCode)  {
        super(status, errorStatusCode);
    }
}
