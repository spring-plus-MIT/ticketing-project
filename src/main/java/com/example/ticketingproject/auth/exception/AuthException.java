package com.example.ticketingproject.auth.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends BaseException {
    public AuthException(HttpStatus status, ErrorStatus errorStatusCode){
        super(status, errorStatusCode);
    }
}
