package com.example.ticketingproject.domain.reservation.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController<UserDetailsImpl> {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<CommonResponse<ReservationResponse>> createReservation(
            @RequestBody ReservationCreateRequest requestDto, // 이름 변경된 DTO 적용
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ReservationResponse response = reservationService.createReservation(requestDto, userDetails.getId());

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}