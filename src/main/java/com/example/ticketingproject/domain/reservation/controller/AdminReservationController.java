package com.example.ticketingproject.domain.reservation.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.service.AdminReservationService;
import com.example.ticketingproject.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final AdminReservationService adminReservationService;
    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<ReservationResponse>>> getAllReservations(
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page) {

        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        adminReservationService.getAllReservations(converted)
                )
        );
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<CommonResponse<Page<ReservationResponse>>> getReservationsByUser(
            @PathVariable Long userId,
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page) {

        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        adminReservationService.getReservationsByUser(userId, converted)
                )
        );
    }

    @GetMapping("/{userId}/{reservationId}")
    public ResponseEntity<CommonResponse<ReservationResponse>> getOndReservation (
            @PathVariable Long userId,
            @PathVariable Long reservationId
    ) {
       return ResponseEntity.ok(CommonResponse.success(
               SuccessStatus.READ_SUCCESS,
               reservationService.findOneReservation(userId, reservationId)
               )
       );
    }

    @DeleteMapping("/{reservationId}/{userId}")
    public ResponseEntity<CommonResponse<Void>> cancelReservation(
            @PathVariable Long reservationId,
            @PathVariable Long userId
    ) {
        reservationService.cancelReservation(reservationId, userId);

        return ResponseEntity.ok(CommonResponse.success(
                        SuccessStatus.DELETE_SUCCESS,
                        null
                )
        );
    }
}