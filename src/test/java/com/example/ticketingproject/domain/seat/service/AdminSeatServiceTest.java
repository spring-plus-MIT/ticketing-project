package com.example.ticketingproject.domain.seat.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.seatgrade.exeption.SeatGradeException;
import com.example.ticketingproject.domain.seatgrade.repository.SeatGradeRepository;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.exception.VenueException;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.example.ticketingproject.redis.lock.service.LockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

@ExtendWith(MockitoExtension.class)
public class AdminSeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private SeatGradeRepository seatGradeRepository;

    @Mock
    private LockService lockService;

    @InjectMocks
    private SeatTransactionalService seatTransactionalService;

    private Seat seat;
    private Venue venue;
    private SeatGrade seatGrade;
    private PerformanceSession performanceSession;

    @BeforeEach
    void setUp() {
        venue = Venue.builder()
                .name("테스트 공연장")
                .address("서울시 어딘가")
                .totalSeats(100)
                .build();
        ReflectionTestUtils.setField(venue, "id", 1L);

        seatGrade = SeatGrade.builder()
                .performanceSession(performanceSession)
                .gradeName(GradeName.VIP)
                .price(new BigDecimal(10000))
                .totalSeats(20)
                .remainingSeats(10)
                .build();
        ReflectionTestUtils.setField(seatGrade, "id", 1L);

        seat = Seat.builder()
                .venue(venue)
                .seatGrade(seatGrade)
                .rowName("A")
                .seatNumber(1)
                .build();
        ReflectionTestUtils.setField(seat, "id", 1L);
    }

    @Test
    @DisplayName("좌석 생성 성공")
    void save_success() {
        CreateSeatRequest request = mock(CreateSeatRequest.class);
        given(request.getGradeName()).willReturn(GradeName.VIP);
        given(request.getRowName()).willReturn("A");
        given(request.getSeatNumber()).willReturn(1);

        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));
        given(seatGradeRepository.findByGradeName(GradeName.VIP)).willReturn(Optional.of(seatGrade));
        given(seatRepository.countByVenueId(1L)).willReturn(10);
        given(seatRepository.save(any(Seat.class))).willReturn(seat);

        SeatResponse response = SeatResponse.from(seatTransactionalService.saveSeat(1L, request));

        assertThat(response.getSeatId()).isEqualTo(1L);
        assertThat(response.getVenueId()).isEqualTo(1L);
        assertThat(response.getGradeName()).isEqualTo(GradeName.VIP);
        assertThat(response.getRowName()).isEqualTo("A");
        assertThat(response.getSeatNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("좌석 생성 실패 - SeatGrade 없음")
    void save_fail_seat_grade_not_found() {
        CreateSeatRequest request = mock(CreateSeatRequest.class);
        given(request.getGradeName()).willReturn(GradeName.VIP);

        given(venueRepository.findById(1L)).willReturn(Optional.of(venue));
        given(seatGradeRepository.findByGradeName(GradeName.VIP)).willReturn(Optional.empty());

        assertThatThrownBy(() -> seatTransactionalService.saveSeat(1L, request))
                .isInstanceOf(SeatGradeException.class);
    }

    @Test
    @DisplayName("좌석 생성 실패 - 좌석 수 초과")
    void save_fail_seat_capacity_exceeded() {
        CreateSeatRequest request = mock(CreateSeatRequest.class);

        given(venueRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> seatTransactionalService.saveSeat(999L, request))
                .isInstanceOf(VenueException.class);
    }
}
