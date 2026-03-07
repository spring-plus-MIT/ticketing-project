package com.example.ticketingproject.common.dto;

import com.example.ticketingproject.common.enums.SuccessStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class CommonResponse<T> {
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String code;
    private final String message;
    private final T data;

    public CommonResponse(LocalDateTime timestamp, HttpStatus status, String code, String message, T data) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> success(SuccessStatus successStatus, String code, String message, T data) {
        return new CommonResponse<>(
                LocalDateTime.now(),
                successStatus.getHttpStatus(),
                successStatus.getSuccessCode(),
                successStatus.getMessage(),
                data
        );
    }
}
