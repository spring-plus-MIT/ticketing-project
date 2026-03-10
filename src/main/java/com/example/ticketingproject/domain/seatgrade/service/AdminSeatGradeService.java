package com.example.ticketingproject.domain.seatgrade.service;

import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import com.example.ticketingproject.domain.seatgrade.dto.CreateSeatGradeRequest;
import com.example.ticketingproject.domain.seatgrade.dto.PutSeatGradeRequest;
import com.example.ticketingproject.domain.seatgrade.dto.SeatGradeResponse;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.exeption.SeatGradeException;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional()
public class AdminSeatGradeService {

    private final SeatGradeRepository seatGradeRepository;
    private final PerformanceSessionRepository performanceSessionRepository;


    public SeatGradeResponse save(Long sessionId, CreateSeatGradeRequest request) {
        PerformanceSession performanceSession = performanceSessionRepository.findById(sessionId).orElseThrow(
                () -> new PerformanceSessionException(SESSION_NOT_FOUND.getHttpStatus(), SESSION_NOT_FOUND)
        );

        SeatGrade seatGrade = SeatGrade.builder()
                .performanceSession(performanceSession)
                .gradeName(request.getGradeName())
                .price(request.getPrice())
                .totalSeats(request.getTotalSeats())
                .remainingSeats(request.getRemainingSeats())
                .build();

        SeatGrade savedSeatGrade = seatGradeRepository.save(seatGrade);

        return SeatGradeResponse.from(savedSeatGrade);
    }

    public SeatGradeResponse update(Long sessionId, Long seatGradeId, PutSeatGradeRequest request) {
        SeatGrade seatGrade = seatGradeRepository.findByIdAndPerformanceSessionId(seatGradeId, sessionId).orElseThrow(
                () -> new SeatGradeException(SEAT_GRADE_NOT_FOUND.getHttpStatus(), SEAT_GRADE_NOT_FOUND)
        );

        seatGrade.update(request.getGradeName());

        return SeatGradeResponse.from(seatGrade);
    }

    public void delete(Long sessionId, Long seatGradeId) {
        SeatGrade seatGrade = seatGradeRepository.findByIdAndPerformanceSessionId(seatGradeId, sessionId).orElseThrow(
                () -> new SeatGradeException(SEAT_GRADE_NOT_FOUND.getHttpStatus(), SEAT_GRADE_NOT_FOUND)
        );

        seatGrade.delete();
    }
}
