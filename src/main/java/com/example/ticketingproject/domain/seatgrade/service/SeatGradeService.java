package com.example.ticketingproject.domain.seatgrade.service;

import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatGradeService {

    private final SeatGradeRepository seatGradeRepository;
}
