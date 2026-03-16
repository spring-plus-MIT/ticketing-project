package com.example.ticketingproject.chat.domain.chat.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ChatException extends BaseException {
    public ChatException(HttpStatus status, ErrorStatus errorStatusCode)  {
        super(status, errorStatusCode);
    }

}
