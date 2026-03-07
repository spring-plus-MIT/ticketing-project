package com.example.ticketingproject.common.dto;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Getter
public class CommonResponse<T> {
    private final LocalDateTime timestamp;
    private final HttpStatusCode status;
    private final String message;
    private final T data;

    public CommonResponse(LocalDateTime timestamp, HttpStatusCode status, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> success(HttpStatusCode status, String message, T data) {
        return new CommonResponse<>(LocalDateTime.now(), status, message, data);
    }
}
