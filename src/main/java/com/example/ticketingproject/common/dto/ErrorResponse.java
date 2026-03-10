package com.example.ticketingproject.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String code;
    private final String message;
    private final String path;

    public static ErrorResponse of(HttpStatus status, String code, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, code, message, path);
    }
}
