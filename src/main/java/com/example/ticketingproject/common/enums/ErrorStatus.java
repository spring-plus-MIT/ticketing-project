package com.example.ticketingproject.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
@Getter
@AllArgsConstructor
public enum ErrorStatus {
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "관리자가 존재하지 않습니다."),
    PASSWORD_CONFIRM_MISMATCH_ERROR(HttpStatus.BAD_REQUEST, "새 비밀번호가 일치하지 않습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다");

    private final HttpStatusCode statusCode;
    private final String message;
}
