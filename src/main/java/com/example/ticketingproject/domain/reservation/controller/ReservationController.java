package com.example.ticketingproject.domain.reservation.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.service.ReservationService;
import com.example.ticketingproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations") // API 명세에 맞게 /reservations
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<CommonResponse<ReservationResponse>> createReservation(
            @RequestBody ReservationCreateRequest requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        // 사용자 ID 바로 꺼내서 서비스 호출
        Long userId = customUserDetails.getId();

        ReservationResponse response = reservationService.createReservation(requestDto, userId);

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
