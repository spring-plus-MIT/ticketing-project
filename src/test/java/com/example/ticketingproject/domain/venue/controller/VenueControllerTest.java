package com.example.ticketingproject.domain.venue.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.service.VenueService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VenueController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class VenueControllerTest extends RestDocsSupport {

    @MockBean
    private VenueService venueService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("공연장 목록 조회 성공")
    @WithMockUser
    void get_venues_success() throws Exception {
        // given
        VenueResponse response = VenueResponse.builder()
                .id(1L)
                .name("예술의 전당")
                .address("서울특별시 서초구")
                .totalSeats(1500)
                .build();

        Page<VenueResponse> pageResponse = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        given(venueService.getVenues(any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/venues")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].name").value("예술의 전당"))
                .andDo(restDocsHandler("venue-get-list"));
    }

    @Test
    @DisplayName("공연장 상세 조회 성공")
    @WithMockUser
    void get_venue_success() throws Exception {
        // given
        Long venueId = 1L;
        VenueResponse response = VenueResponse.builder()
                .id(venueId)
                .name("예술의 전당")
                .address("서울특별시 서초구")
                .totalSeats(1500)
                .build();

        given(venueService.getVenue(venueId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/venues/{venueId}", venueId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(venueId))
                .andExpect(jsonPath("$.data.name").value("예술의 전당"))
                .andDo(restDocsHandler("venue-get-detail"));
    }
}