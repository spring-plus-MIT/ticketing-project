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
                () -> new PerformanceSessionException(PERFORMANCE_SESSION_NOT_FOUND.getHttpStatus(), PERFORMANCE_SESSION_NOT_FOUND)
        );

        SeatGrade seatGrade = SeatGrade.builder()
                .performanceSession(performanceSession)
                .gradeName(request.getGradeName())
                .price(request.getPrice())
                .totalSeats(request.getTotalSeats())
                .remainingSeats(request.getRemainingSeats())
                .build();

        SeatGrade savedSeatGrade = seatGradeRepository.save(seatGrade);

        return SeatGradeResponse.builder()
                .seatGradeId(savedSeatGrade.getId())
                .sessionId(savedSeatGrade.getPerformanceSession().getId())
                .gradeName(savedSeatGrade.getGradeName())
                .price(savedSeatGrade.getPrice())
                .totalSeats(savedSeatGrade.getTotalSeats())
                .remainingSeats(savedSeatGrade.getRemainingSeats())
                .createdAt(savedSeatGrade.getCreatedAt())
                .modifiedAt(savedSeatGrade.getModifiedAt())
                .deletedAt(savedSeatGrade.getDeletedAt())
                .build();
    }

    public SeatGradeResponse update(Long sessionId, Long seatGradeId, PutSeatGradeRequest request) {
        SeatGrade seatGrade = seatGradeRepository.findByIdAndSessionIdAndDeletedAtIsNull(seatGradeId, sessionId).orElseThrow(
                () -> new SeatGradeException(SEAT_GRADE_NOT_FOUND.getHttpStatus(), SEAT_GRADE_NOT_FOUND)
        );

        seatGrade.update(request.getGradeName());

        return SeatGradeResponse.builder()
                .seatGradeId(seatGrade.getId())
                .sessionId(seatGrade.getPerformanceSession().getId())
                .gradeName(seatGrade.getGradeName())
                .price(seatGrade.getPrice())
                .totalSeats(seatGrade.getTotalSeats())
                .remainingSeats(seatGrade.getRemainingSeats())
                .createdAt(seatGrade.getCreatedAt())
                .modifiedAt(seatGrade.getModifiedAt())
                .deletedAt(seatGrade.getDeletedAt())
                .build();
    }

    public void delete(Long sessionId, Long seatGradeId) {
        SeatGrade seatGrade = seatGradeRepository.findByIdAndSessionIdAndDeletedAtIsNull(seatGradeId, sessionId).orElseThrow(
                () -> new SeatGradeException(SEAT_GRADE_NOT_FOUND.getHttpStatus(), SEAT_GRADE_NOT_FOUND)
        );

        seatGrade.delete();
    }
}
