package com.example.ticketingproject.domain.seat.service;

import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
}
