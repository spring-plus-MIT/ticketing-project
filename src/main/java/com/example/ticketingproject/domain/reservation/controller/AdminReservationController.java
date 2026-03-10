package com.example.ticketingproject.domain.reservation.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponseDto;
import com.example.ticketingproject.domain.reservation.service.AdminReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/reservations")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;

    /**
     * [관리자] 전체 예약 목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResponse<Page<ReservationResponseDto>>> getAllReservations(
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page) {

        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        SuccessStatus.READ_SUCCESS.getSuccessCode(),
                        SuccessStatus.READ_SUCCESS.getMessage(),
                        adminReservationService.getAllReservations(converted)
                )
        );
    }

    /**
     * [관리자] 특정 유저의 예약 목록 조회
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<CommonResponse<Page<ReservationResponseDto>>> getReservationsByUser(
            @PathVariable Long userId,
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page) {

        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        SuccessStatus.READ_SUCCESS.getSuccessCode(),
                        SuccessStatus.READ_SUCCESS.getMessage(),
                        adminReservationService.getReservationsByUser(userId, converted)
                )
        );
    }
}