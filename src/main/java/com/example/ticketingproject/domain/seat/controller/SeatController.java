package com.example.ticketingproject.domain.seat.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/venues/{venueId}/seats")
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<SeatResponse>>> getAll(
            @PathVariable(name = "venueId")  Long venueId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, seatService.findAll(venueId, pageable)));
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<CommonResponse<SeatResponse>> getOne(@PathVariable(name = "venueId") Long venueId, @PathVariable Long seatId) {
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, seatService.findOne(venueId, seatId)));
    }
}
