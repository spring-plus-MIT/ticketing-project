package com.example.ticketingproject.domain.seat.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.service.AdminSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/venues/{venueId}/seats")
public class AdminSeatController {

    private final AdminSeatService adminSeatService;

    @PostMapping()
    public ResponseEntity<CommonResponse<SeatResponse>> create(@Valid @PathVariable(name = "venueId") Long venueId, CreateSeatRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(CREATE_SUCCESS, CREATE_SUCCESS.getSuccessCode(), CREATE_SUCCESS.getMessage(), adminSeatService.save(venueId, request)));
    }
}
