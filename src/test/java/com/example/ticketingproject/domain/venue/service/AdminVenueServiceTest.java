package com.example.ticketingproject.domain.venue.service;

import com.example.ticketingproject.domain.venue.dto.CreateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AdminVenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private AdminVenueService adminVenueService;

    @Test
    void create_공연장_생성_성공() {
        // given
        CreateVenueRequest request = mock(CreateVenueRequest.class);
        given(request.getName()).willReturn("올림픽 경기장");
        given(request.getAddress()).willReturn("서울특별시 송파구 올림픽로 424");
        given(request.getTotalSeats()).willReturn(50000);

        Venue savedVenue = Venue.builder()
                .name("올림픽 경기장")
                .address("서울특별시 송파구 올림픽로 424")
                .totalSeats(50000)
                .build();

        given(venueRepository.save(any(Venue.class))).willReturn(savedVenue);

        // when
        VenueResponse response = adminVenueService.create(request);

        // then
        assertThat(response.getName()).isEqualTo("올림픽 경기장");
        assertThat(response.getAddress()).isEqualTo("서울특별시 송파구 올림픽로 424");
        assertThat(response.getTotalSeats()).isEqualTo(50000);
    }
}
