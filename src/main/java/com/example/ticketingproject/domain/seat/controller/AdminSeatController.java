package com.example.ticketingproject.domain.seat.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.service.AdminSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/venues/{venueId}/seats")
public class AdminSeatController {

    private final AdminSeatService adminSeatService;

    @PostMapping
    public ResponseEntity<CommonResponse<SeatResponse>> create(@PathVariable(name = "venueId") Long venueId, @Valid @RequestBody CreateSeatRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(CREATE_SUCCESS, adminSeatService.save(venueId, request)));
    }
}
