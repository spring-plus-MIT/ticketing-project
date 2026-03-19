package com.example.ticketingproject.common.util;

public class Constants {

    // Auth
    public static final String MSG_LOGIN_FAILED = "이메일 또는 비밀번호가 올바르지 않습니다";
    public static final String MSG_INVALID_TOKEN = "유효하지 않은 토큰입니다";
    public static final String MSG_TOKEN_EXPIRED = "토큰이 만료되었습니다";
    public static final String MSG_INVALID_SIGNATURE = "토큰 서명이 유효하지 않습니다";
    public static final String MSG_ACCESS_FORBIDDEN = "권한이 부족합니다";

    // User
    public static final String MSG_DUPLICATE_EMAIL = "이미 사용 중인 이메일입니다";
    public static final String MSG_USER_NOT_FOUND = "존재하지 않는 유저입니다";
    public static final String MSG_ADMIN_NOT_FOUND = "관리자가 존재하지 않습니다";
    public static final String MSG_INVALID_PASSWORD = "비밀번호가 일치하지 않습니다";
    public static final String MSG_ALREADY_DELETED_USER = "이미 탈퇴한 회원 입니다";

    // Work
    public static final String MSG_WORK_NOT_FOUND = "작품이 존재하지 않습니다";

    // Review
    public static final String MSG_REVIEW_NOT_FOUND = "리뷰가 존재하지 않습니다";

    // CastMember
    public static final String MSG_CAST_MEMBER_NOT_FOUND = "출연진이 존재하지 않습니다";

    // Venue
    public static final String MSG_VENUE_NOT_FOUND = "장소가 존재하지 않습니다";

    // Seat
    public static final String MSG_SEAT_NOT_FOUND = "좌석이 존재하지 않습니다";
    public static final String MSG_SEAT_CAPACITY_EXCEEDED = "Venue의 총 좌석 수를 초과할 수 없습니다";
    public static final String MSG_ALREADY_RESERVED_SEAT = "이미 예약된 좌석입니다";
    public static final String MSG_ALREADY_SOLD_SEAT = "이미 판매된 좌석입니다";
    public static final String MSG_DUPLICATE_SEAT = "이미 존재하는 좌석 입니다";

    // Performance
    public static final String MSG_PERFORMANCE_NOT_FOUND = "공연이 존재하지 않습니다";

    // Session
    public static final String MSG_SESSION_NOT_FOUND = "공연 회차가 존재하지 않습니다";
    public static final String MSG_DUPLICATE_SESSION = "해당 장소에 동일한 시간의 공연 회차가 이미 존재합니다";

    // SeatGrade
    public static final String MSG_SEAT_GRADE_NOT_FOUND = "좌석 등급이 존재하지 않습니다";
    public static final String MSG_SEAT_GRADE_CAPACITY_EXCEEDED = "해당 좌석 등급의 잔여 좌석이 없습니다";

    // Like
    public static final String MSG_LIKE_NOT_FOUND = "찜이 존재하지 않습니다";
    public static final String MSG_LIKE_ALREADY_EXISTS = "이미 찜한 작품입니다";

    // Payment
    public static final String MSG_PAYMENT_NOT_FOUND = "존재하지 않는 결제입니다";
    public static final String MSG_INSUFFICIENT_BALANCE = "잔액이 부족합니다";

    // Reservation
    public static final String MSG_ALREADY_CANCELLED_RESERVATION = "이미 취소된 예약 입니다";
    public static final String MSG_ALREADY_PAID_RESERVATION = "이미 결제된 예약 입니다";

    // Validation
    public static final String MSG_VALIDATION_NOT_BLANK_ERROR = "필수 입력 값이 누락 되었습니다";
    public static final String MSG_VALIDATION_NOT_NULL_ERROR = "필수 입력 값이 누락 되었습니다";
    public static final String MSG_VALIDATION_EMAIL_ERROR = "이메일 형식이 올바르지 않습니다";
    public static final String MSG_VALIDATION_LENGTH_ERROR = "입력 길이가 잘못되었습니다";
    public static final String MSG_VALIDATION_PATTERN_ERROR = "입력 형식이 잘못되었습니다";
    public static final String MSG_VALIDATION_DIGITS_ERROR = "금액은 소수점 2자리 부터 천만원 단위 까지만 입력 가능합니다";
    public static final String MSG_VALIDATION_DECIMAL_MIN_ERROR = "금액은 음수일 수 없습니다";

    // Lock
    public static final String MSG_LOCK_ACQUISITION_FAILED = "다른 사용자가 이미 요청 중입니다";
    public static final String MSG_LOCK_TIMEOUT = "요청이 많아 처리가 지연되었습니다. 잠시 후 다시 시도해주세요";
    public static final String MSG_LOCK_RELEASE_FAILED = "요청 처리 중 문제가 발생했습니다. 다시 시도해주세요";
    public static final String MSG_LOCK_INTERRUPTED = "요청이 정상적으로 처리되지 않았습니다. 잠시 후 다시 시도해주세요";
    // Chatroom
    public static final String MSG_CHAT_ROOM_NOT_FOUND = "존재하지 않는 채팅방입니다";
    public static final String MSG_FORBIDDEN_CHAT_ROOM = "해당 채팅방에 접근할 권한이 없습니다";
    public static final String MSG_CHAT_ROOM_ALREADY_COMPLETED = "이미 종료된 상담입니다";
    public static final String MSG_INVALID_STATUS_TRANSITION = "올바르지 않은 상태 변경입니다";



}
