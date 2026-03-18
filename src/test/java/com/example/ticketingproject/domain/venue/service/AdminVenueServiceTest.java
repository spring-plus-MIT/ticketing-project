package com.example.ticketingproject.domain.venue.service;

import com.example.ticketingproject.domain.venue.dto.CreateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.UpdateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.exception.VenueException;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AdminVenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private AdminVenueService adminVenueService;

    private Venue venue;

    @BeforeEach
    void setUp() {
        venue = Venue.builder()
                .name("올림픽 경기장")
                .address("서울특별시 송파구 올림픽로 424")
                .totalSeats(50000)
                .build();
        ReflectionTestUtils.setField(venue, "id", 1L);
    }

    @Test
    void create_공연장_생성_성공() {
        // given
        CreateVenueRequest request = mock(CreateVenueRequest.class);
        given(request.getName()).willReturn("올림픽 경기장");
        given(request.getAddress()).willReturn("서울특별시 송파구 올림픽로 424");
        given(request.getTotalSeats()).willReturn(50000);

        given(venueRepository.save(any(Venue.class))).willReturn(venue);

        // when
        VenueResponse response = adminVenueService.create(request);

        // then
        assertThat(response.getName()).isEqualTo("올림픽 경기장");
        assertThat(response.getAddress()).isEqualTo("서울특별시 송파구 올림픽로 424");
        assertThat(response.getTotalSeats()).isEqualTo(50000);
    }

    @Nested
    class UpdateVenue {

        @Test
        void updateVenue_공연장_수정_성공() {
            // given
            UpdateVenueRequest request = mock(UpdateVenueRequest.class);
            given(request.getName()).willReturn("잠실 실내체육관");
            given(request.getAddress()).willReturn("서울특별시 송파구 올림픽로 25");
            given(request.getTotalSeats()).willReturn(10000);

            given(venueRepository.findById(1L)).willReturn(Optional.of(venue));

            // when
            VenueResponse response = adminVenueService.updateVenue(1L, request);

            // then
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("잠실 실내체육관");
            assertThat(response.getAddress()).isEqualTo("서울특별시 송파구 올림픽로 25");
            assertThat(response.getTotalSeats()).isEqualTo(10000);
        }

        @Test
        void updateVenue_존재하지_않는_공연장_수정_실패() {
            // given
            UpdateVenueRequest request = mock(UpdateVenueRequest.class);
            given(venueRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminVenueService.updateVenue(999L, request))
                    .isInstanceOf(VenueException.class);
        }
    }

    @Nested
    class DeleteVenue {

        @Test
        void deleteVenue_공연장_삭제_성공() {
            // given
            given(venueRepository.findById(1L)).willReturn(Optional.of(venue));

            // when
            adminVenueService.deleteVenue(1L);

            // then
            assertThat(venue.isDeleted()).isTrue();
            assertThat(venue.getDeletedAt()).isNotNull();
        }

        @Test
        void deleteVenue_존재하지_않는_공연장_삭제_실패() {
            // given
            given(venueRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> adminVenueService.deleteVenue(999L))
                    .isInstanceOf(VenueException.class);
        }
    }

}
