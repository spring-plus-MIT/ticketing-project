package com.example.ticketingproject.domain.venue.service;

import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    @Test
    void getVenues_정상_반환() {
        // given
        Venue venue1 = Venue.builder().name("올림픽 경기장").address("서울특별시 송파구 올림픽로 424").totalSeats(50000).build();
        ReflectionTestUtils.setField(venue1, "id", 1L);

        Venue venue2 = Venue.builder().name("잠실 실내체육관").address("서울특별시 송파구 잠실동 10").totalSeats(15000).build();
        ReflectionTestUtils.setField(venue2, "id", 2L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Venue> venuePage = new PageImpl<>(List.of(venue1, venue2), pageable, 2);

        given(venueRepository.findAll(pageable)).willReturn(venuePage);

        // when
        Page<VenueResponse> result = venueService.getVenues(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);

        VenueResponse response1 = result.getContent().get(0);
        assertThat(response1.getId()).isEqualTo(1L);
        assertThat(response1.getName()).isEqualTo("올림픽 경기장");
        assertThat(response1.getAddress()).isEqualTo("서울특별시 송파구 올림픽로 424");

        VenueResponse response2 = result.getContent().get(1);
        assertThat(response2.getId()).isEqualTo(2L);
        assertThat(response2.getName()).isEqualTo("잠실 실내체육관");
        assertThat(response2.getAddress()).isEqualTo("서울특별시 송파구 잠실동 10");

        then(venueRepository).should().findAll(pageable);
    }

    @Test
    void getVenues_빈_페이지_반환() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Venue> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        given(venueRepository.findAll(pageable)).willReturn(emptyPage);

        // when
        Page<VenueResponse> result = venueService.getVenues(pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        then(venueRepository).should().findAll(pageable);
    }

    @Test
    void getVenues_페이지네이션_정보_검증() {
        // given
        List<Venue> venues = List.of(
                Venue.builder().name("공연장A").address("주소A").totalSeats(1000).build(),
                Venue.builder().name("공연장B").address("주소B").totalSeats(2000).build()
        );

        Pageable pageable = PageRequest.of(1, 2);
        Page<Venue> venuePage = new PageImpl<>(venues, pageable, 6);

        given(venueRepository.findAll(pageable)).willReturn(venuePage);

        // when
        Page<VenueResponse> result = venueService.getVenues(pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(6);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(2);

        then(venueRepository).should().findAll(pageable);
    }
}
