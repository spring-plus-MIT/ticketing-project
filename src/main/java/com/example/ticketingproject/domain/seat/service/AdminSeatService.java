package com.example.ticketingproject.domain.seat.service;

import com.example.ticketingproject.common.exception.BaseException;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.VENUE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSeatService {

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public SeatResponse save(Long venueId, CreateSeatRequest request) {
        Venue venue = venueRepository.findById(venueId).orElseThrow(
                () -> new BaseException(VENUE_NOT_FOUND.getHttpStatus(), VENUE_NOT_FOUND)
        );

        Seat seat = Seat.builder()
                .venue(venue)
                .gradeName(request.getGradeName())
                .rowName(request.getRowName())
                .seatNumber(request.getSeatNumber())
                .build();

        Seat savedSeat = seatRepository.save(seat);

        return SeatResponse.builder()
                .seatId(savedSeat.getId())
                .venueId(savedSeat.getVenue().getId())
                .gradeName(savedSeat.getGradeName())
                .rowName(savedSeat.getRowName())
                .seatNumber(savedSeat.getSeatNumber())
                .createdAt(savedSeat.getCreatedAt())
                .build();
    }
}
