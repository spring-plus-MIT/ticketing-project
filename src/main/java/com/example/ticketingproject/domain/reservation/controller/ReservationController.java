package com.example.ticketingproject.domain.reservation.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.service.ReservationService;
import com.example.ticketingproject.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<CommonResponse<ReservationResponse>> createReservation(
            @RequestBody @Valid ReservationCreateRequest requestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ReservationResponse response = reservationService.createReservation(requestDto, customUserDetails.getId());
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.CREATE_SUCCESS, response));
    }
}
