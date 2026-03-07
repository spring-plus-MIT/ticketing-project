package com.example.ticketingproject.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum ErrorStatus {
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "400_LOGIN_FAILED", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "401_INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401_TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "400_DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "404_USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "404_ADMIN_NOT_FOUND", "관리자가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "400_INVALID_PASSWORD", "비밀번호가 일치하지 않습니다."),
    ALREADY_DELETED_USER(HttpStatus.GONE, "410_ALREADY_DELETED_USER", "이미 탈퇴한 회원 입니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "404_REVIEW_NOT_FOUND", "리뷰가 존재하지 않습니다");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}