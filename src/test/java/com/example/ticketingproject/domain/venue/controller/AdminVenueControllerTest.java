package com.example.ticketingproject.domain.venue.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.venue.dto.CreateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.UpdateVenueRequest;
import com.example.ticketingproject.domain.venue.dto.VenueResponse;
import com.example.ticketingproject.domain.venue.service.AdminVenueService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminVenueController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminVenueControllerTest extends RestDocsSupport {

    @MockBean
    private AdminVenueService adminVenueService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("관리자 - 공연장 등록 성공")
    @WithMockUser(roles = "ADMIN")
    void create_venue_success() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "예술의 전당",
                    "address": "서울특별시 서초구 남부순환로 2406",
                    "totalSeats": 1500
                }
                """;

        VenueResponse response = VenueResponse.builder()
                .id(1L)
                .name("예술의 전당")
                .address("서울특별시 서초구 남부순환로 2406")
                .totalSeats(1500)
                .build();

        given(adminVenueService.create(any(CreateVenueRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/admin/venues")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("예술의 전당"))
                .andExpect(jsonPath("$.data.totalSeats").value(1500))
                .andDo(restDocsHandler("admin-venue-create"));
    }

    @Test
    @DisplayName("관리자 - 공연장 정보 수정 성공")
    @WithMockUser(roles = "ADMIN")
    void update_venue_success() throws Exception {
        // given
        Long venueId = 1L;
        String requestBody = """
                {
                    "name": "수정된 공연장 이름",
                    "address": "수정된 주소",
                    "totalSeats": 2000
                }
                """;

        VenueResponse response = VenueResponse.builder()
                .id(venueId)
                .name("수정된 공연장 이름")
                .address("수정된 주소")
                .totalSeats(2000)
                .build();

        given(adminVenueService.updateVenue(eq(venueId), any(UpdateVenueRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(patch("/admin/venues/{venueId}", venueId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("수정된 공연장 이름"))
                .andDo(restDocsHandler("admin-venue-update"));
    }

    @Test
    @DisplayName("관리자 - 공연장 삭제 성공")
    @WithMockUser(roles = "ADMIN")
    void delete_venue_success() throws Exception {
        // given
        Long venueId = 1L;
        willDoNothing().given(adminVenueService).deleteVenue(venueId);

        // when & then
        mockMvc.perform(delete("/admin/venues/{venueId}", venueId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("admin-venue-delete"));
    }
}