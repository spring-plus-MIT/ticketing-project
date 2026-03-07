package com.example.ticketingproject.common.config;

import com.example.ticketingproject.common.dto.ErrorResponse;
import com.example.ticketingproject.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> BaseExceptionHandler(
            BaseException e, HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getErrorStatusCode().getHttpStatus(),
                e.getErrorStatusCode().getErrorCode(),
                e.getErrorStatusCode().getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(e.getErrorStatusCode().getHttpStatus()).body(errorResponse);
    }
}
