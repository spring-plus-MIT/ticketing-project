package com.example.ticketingproject.domain.seat.service;

import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.exception.SeatException;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

    private final SeatRepository seatRepository;

    public Page<SeatResponse> findAll(Long venueId, Pageable pageable) {
        return seatRepository.findAllByVenueId(venueId, pageable)
                .map(SeatResponse::from);
    }

    public SeatResponse findOne(Long venueId, Long seatId) {
        Seat seat = seatRepository.findByIdAndVenueId(seatId, venueId).orElseThrow(
                () -> new SeatException(SEAT_NOT_FOUND.getHttpStatus(), SEAT_NOT_FOUND)
        );

        return SeatResponse.from(seat);
    }
}
