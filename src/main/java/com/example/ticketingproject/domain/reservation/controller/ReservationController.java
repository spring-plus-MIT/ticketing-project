package com.example.ticketingproject.domain.reservation.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
// ⭐ SuccessStatus의 위치를 가장 확률이 높은 common.enums로 수정했습니다.
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.security.CustomUserDetails;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationRequestDto;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponseDto;
import com.example.ticketingproject.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<CommonResponse<ReservationResponseDto>> createReservation(
            @RequestBody ReservationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(
                        SuccessStatus.CREATE_SUCCESS,
                        SuccessStatus.CREATE_SUCCESS.getSuccessCode(),
                        SuccessStatus.CREATE_SUCCESS.getMessage(),
                        // 서비스에 유저 ID(Long)를 전달합니다.
                        reservationService.createReservation(requestDto, userDetails.getId())
                ));
    }
}