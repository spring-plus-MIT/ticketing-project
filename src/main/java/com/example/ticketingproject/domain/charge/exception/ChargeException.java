package com.example.ticketingproject.domain.charge.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChargeException extends BaseException {
    public ChargeException(HttpStatus status, ErrorStatus errorStatusCode) {
        super(status, errorStatusCode);
    }
}
