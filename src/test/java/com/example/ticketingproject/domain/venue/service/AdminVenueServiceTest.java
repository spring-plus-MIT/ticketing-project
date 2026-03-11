package com.example.ticketingproject.domain.venue.service;

import com.example.ticketingproject.domain.venue.dto.CreateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.UpdateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.venue.repository.VenueRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Mock
    private ObjectMapper objectMapper;

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

    @Test
    void 장소_수정_성공_테스트() throws JsonProcessingException {

        // given
        Venue venue = Venue.builder()
                .name("장소 이름")
                .address("장소 주소")
                .totalSeats(300)
                .build();

        venueRepository.save(venue);

        String json = """
                {
                    "name": "장소 이름 수정",
                    "address": "장소 주소 수정",
                    "totalSeats": 250
                }
                """;

        UpdateVenueRequest request = objectMapper.readValue(json, UpdateVenueRequest.class);

        // when
        VenueResponse response = adminVenueService.updateVenue(venue.getId(), request);

        //then
        assertThat(response.getName()).isEqualTo("장소 이름 수정");
        assertThat(response.getAddress()).isEqualTo("장소 주소 이름");

        Venue updated = venueRepository.findById(venue.getId()).orElseThrow();

        assertThat(updated.getName()).isEqualTo("장소 이름 수정");
        assertThat(updated.getAddress()).isEqualTo("장소 주소 수정");
        assertThat(updated.getTotalSeats()).isEqualTo(250);
    }

    @Test
    void 장소_삭제_성공_테스트() {

        // given
        Venue venue = Venue.builder()
                .name("장소 이름")
                .address("장소 주소")
                .totalSeats(300)
                .build();

        venueRepository.save(venue);

        // when
        adminVenueService.deleteVenue(venue.getId());

        // then
        Venue deleted = venueRepository.findById(venue.getId()).orElseThrow();
        assertThat(deleted.getDeletedAt()).isNotNull();
        assertThat(deleted.isDeleted()).isTrue();
    }
}
