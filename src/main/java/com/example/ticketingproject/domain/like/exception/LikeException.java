package com.example.ticketingproject.domain.like.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LikeException extends BaseException {
    public LikeException(HttpStatus status, ErrorStatus errorStatusCode) {
        super(status, errorStatusCode);
    }
}
