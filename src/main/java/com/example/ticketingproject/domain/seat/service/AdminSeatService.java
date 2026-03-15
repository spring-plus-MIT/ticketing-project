package com.example.ticketingproject.domain.seat.service;

import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.enums.SeatStatus;
import com.example.ticketingproject.domain.seat.exception.SeatException;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.exeption.SeatGradeException;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.exception.VenueException;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.lock.service.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminSeatService {

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;
    private final SeatGradeRepository seatGradeRepository;
    private final LockService lockService;

    public SeatResponse save(Long venueId, CreateSeatRequest request) {

        String key = lockService.createVenueAndSeatLockKey(venueId);
        String uuid = lockService.lock(key);

        try {
            Venue venue = venueRepository.findById(venueId).orElseThrow(
                    () -> new VenueException(VENUE_NOT_FOUND.getHttpStatus(), VENUE_NOT_FOUND)
            );

            SeatGrade seatGrade = seatGradeRepository.findByGradeName(request.getGradeName()).orElseThrow(
                    () -> new SeatGradeException(SEAT_GRADE_NOT_FOUND.getHttpStatus(), SEAT_GRADE_NOT_FOUND)
            );

            int currentSeatCount = seatRepository.countByVenueId(venue.getId());

            if(currentSeatCount >= venue.getTotalSeats()) {
                throw new SeatException(SEAT_CAPACITY_EXCEEDED.getHttpStatus(), SEAT_CAPACITY_EXCEEDED);
            }

            seatGrade.decreaseRemainingSeats();

            Seat seat = Seat.builder()
                    .venue(venue)
                    .seatGrade(seatGrade)
                    .rowName(request.getRowName())
                    .seatNumber(request.getSeatNumber())
                    .seatStatus(SeatStatus.AVAILABLE)
                    .build();

            Seat savedSeat = seatRepository.save(seat);

            return SeatResponse.from(savedSeat);
        } finally {
            lockService.unlock(key, uuid);
        }
    }
}
