package com.example.ticketingproject.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    REGISTER_SUCCESS(HttpStatus.CREATED, "201_REGISTER_SUCCESS", "회원가입에 성공하였습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "200_LOGIN_SUCCESS", "로그인에 성공하였습니다."),
    FOUND_SUCCESS(HttpStatus.OK, "200_FOUND_SUCCESS", "사용자 조회 성공"),
    LOGOUT_SUCCESS(HttpStatus.OK, "200_LOGOUT_SUCCESS", "로그아웃에 성공하였습니다.");

    private final HttpStatus httpStatus;
    private final String successCode;
    private final String message;
}
