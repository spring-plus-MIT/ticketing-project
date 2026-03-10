package com.example.ticketingproject.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String code;
    private List<ValidationError> messages;
    private String path;

    public static ValidationErrorResponse of(
            HttpStatus status,
            String code,
            List<ValidationError> messages,
            String path
    ) {
        return new ValidationErrorResponse(LocalDateTime.now(), status, code, messages, path);
    }
}
