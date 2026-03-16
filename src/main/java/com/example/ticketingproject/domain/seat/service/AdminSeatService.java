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
import com.example.ticketingproject.redis.lock.annotation.RedisLock;
import com.example.ticketingproject.redis.lock.enums.LockStrategy;
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

    // SpEL 표현식으로 Lock Key 동적 생성
    // SpEL 작성법 = 문자열은 ' '로 감싸고, 메서드 파라미터는 #파라미터명
    // "'Key 시작 이름' + #파라미터값 변수명(추가로 붙일 키 이름은 '' 감싸서 추가)"
    // 예) venueId = 2 -> Lock:venue:2:seat:create
    @RedisLock(key = "'lock:venue:' + #venueId + ':seat:create'", strategy = LockStrategy.RETRY)
    public SeatResponse save(Long venueId, CreateSeatRequest request) {

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
    }
}
