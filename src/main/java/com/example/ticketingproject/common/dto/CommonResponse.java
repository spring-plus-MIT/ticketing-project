package com.example.ticketingproject.common.dto;

import com.example.ticketingproject.common.enums.SuccessStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CommonResponse<T> {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final HttpStatus status;
    private final String code;
    private final String message;
    private final T data;

    public static <T> CommonResponse<T> success(SuccessStatus successStatus, T data) {
        return new CommonResponse<>(
                successStatus.getHttpStatus(),
                successStatus.getSuccessCode(),
                successStatus.getMessage(),
                data
        );
    }

        public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(
                LocalDateTime.now(),
                HttpStatus.OK,
                "SUCCESS",
                "요청에 성공하였습니다.",
                data
        );
    }
}