package com.example.ticketingproject.domain.seatgrade.service;

import com.example.ticketingproject.domain.seatgrade.dto.SeatGradeResponse;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.exeption.SeatGradeException;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class SeatGradeService {

    private final SeatGradeRepository seatGradeRepository;

    public Page<SeatGradeResponse> findAll(Long sessionId, Pageable pageable) {
        return seatGradeRepository.findAllByPerformanceSessionId(sessionId, pageable)
                .map(SeatGradeResponse::from);
    }

    public SeatGradeResponse findOne(Long sessionId, Long seatGradeId) {
        SeatGrade seatGrade = seatGradeRepository.findByIdAndPerformanceSessionId(seatGradeId, sessionId).orElseThrow(
                () -> new SeatGradeException(SEAT_GRADE_NOT_FOUND.getHttpStatus(), SEAT_GRADE_NOT_FOUND)
        );

        return SeatGradeResponse.from(seatGrade);
    }
}
