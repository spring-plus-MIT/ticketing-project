package com.example.ticketingproject.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    REGISTER_SUCCESS(HttpStatus.OK, "회원가입에 성공하였습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공하였습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공하였습니다.");

    private final HttpStatusCode statusCode;
    private final String message;
}
