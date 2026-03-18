package com.example.ticketingproject.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String code;
    private final List<ValidationError> messages;
    private final String path;

    public static ValidationErrorResponse of(
            HttpStatus status,
            String code,
            List<ValidationError> messages,
            String path
    ) {
        return new ValidationErrorResponse(LocalDateTime.now(), status, code, messages, path);
    }
}
