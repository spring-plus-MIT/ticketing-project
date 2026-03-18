package com.example.ticketingproject.domain.seat.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.common.exception.BaseException;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seatgrade.entity.SeatGrade;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private SeatService seatService;

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
                .price(BigDecimal.valueOf(10000))
                .totalSeats(100)
                .remainingSeats(50)
                .build();

        seat = Seat.builder()
                .venue(venue)
                .seatGrade(seatGrade)
                .rowName("A")
                .seatNumber(1)
                .build();
        ReflectionTestUtils.setField(seat, "id", 1L);
    }

    @Test
    @DisplayName("좌석 목록 조회 성공")
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Seat> seatPage = new PageImpl<>(List.of(seat), pageable, 1);

        given(seatRepository.findAllByVenueId(1L, pageable)).willReturn(seatPage);

        Page<SeatResponse> response = seatService.findAll(1L, pageable);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().get(0).getSeatId()).isEqualTo(1L);
        assertThat(response.getContent().get(0).getGradeName()).isEqualTo(GradeName.VIP);
    }

    @Test
    @DisplayName("좌석 목록 조회 - 결과 없음")
    void findAll_empty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Seat> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        given(seatRepository.findAllByVenueId(1L, pageable)).willReturn(emptyPage);

        Page<SeatResponse> response = seatService.findAll(1L, pageable);

        assertThat(response.getTotalElements()).isEqualTo(0);
        assertThat(response.getContent()).isEmpty();
    }

    @Test
    @DisplayName("좌석 단건 조회 성공")
    void findOne_success() {
        given(seatRepository.findByIdAndVenueId(1L, 1L)).willReturn(Optional.of(seat));

        SeatResponse response = seatService.findOne(1L, 1L);

        assertThat(response.getSeatId()).isEqualTo(1L);
        assertThat(response.getVenueId()).isEqualTo(1L);
        assertThat(response.getGradeName()).isEqualTo(GradeName.VIP);
        assertThat(response.getRowName()).isEqualTo("A");
        assertThat(response.getSeatNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("좌석 단건 조회 실패")
    void findOne_fail_seatNotFound() {
        given(seatRepository.findByIdAndVenueId(999L, 1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.findOne(1L, 999L))
                .isInstanceOf(BaseException.class);
    }
}
