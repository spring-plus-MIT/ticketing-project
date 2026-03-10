package com.example.ticketingproject.domain.seat.service;

import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.common.exception.BaseException;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.seat.repository.SeatRepository;
import com.example.ticketingproject.domain.seat.service.AdminSeatService;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminSeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private AdminSeatService adminSeatService;

    private Seat seat;
    private Venue venue;

    @BeforeEach
    void setUp() {
        venue = Venue.builder()
                .name("테스트 공연장")
                .address("서울시 어딘가")
                .totalSeats(100)
                .build();
        ReflectionTestUtils.setField(venue, "id", 1L);

        seat = Seat.builder()
                .venue(venue)
                .gradeName(GradeName.VIP)
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
        given(seatRepository.save(any(Seat.class))).willReturn(seat);

        SeatResponse response = adminSeatService.save(1L, request);

        assertThat(response.getSeatId()).isEqualTo(1L);
        assertThat(response.getVenueId()).isEqualTo(1L);
        assertThat(response.getGradeName()).isEqualTo(GradeName.VIP);
        assertThat(response.getRowName()).isEqualTo("A");
        assertThat(response.getSeatNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("좌석 생성 실패")
    void save_fail() {
        CreateSeatRequest request = mock(CreateSeatRequest.class);

        given(venueRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminSeatService.save(999L, request))
                .isInstanceOf(BaseException.class);
    }
}
