package com.example.ticketingproject.domain.seat.controller;

import com.example.ticketingproject.domain.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatController {

    private final SeatService seatService;
}
