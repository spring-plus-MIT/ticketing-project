package com.example.ticketingproject.domain.review.exception;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.common.exception.BaseException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ReviewException extends BaseException {
    public ReviewException(HttpStatus status, ErrorStatus errorStatusCode) {
        super(status, errorStatusCode);
    }
}