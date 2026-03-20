package com.example.ticketingproject.domain.seat.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.enums.SeatStatus;
import com.example.ticketingproject.domain.seat.service.SeatService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeatController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
public class SeatControllerTest extends RestDocsSupport {

    @MockBean
    private SeatService seatService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser
    void 좌석_목록_조회_테스트() throws Exception {

        // given
        SeatResponse response = SeatResponse.builder()
                .seatId(1L)
                .venueId(1L)
                .gradeName(GradeName.VIP)
                .rowName("A")
                .seatNumber(1)
                .seatStatus(SeatStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .build();

        PageImpl<SeatResponse> pageResponse = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 10),
                1
        );

        given(seatService.findAll(eq(1L), any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/venues/{venueId}/seats", 1L)
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].seatId").value(1L))
                .andExpect(jsonPath("$.data.content[0].seatStatus").value("AVAILABLE"))
                .andExpect(jsonPath("$.data.content[0].createdAt").exists())
                .andDo(restDocsHandler("seat-get-pages"));
    }

    @Test
    @WithMockUser
    void 좌석_단건_조회_테스트() throws Exception {

        // given
        SeatResponse response = SeatResponse.builder()
                .seatId(2L)
                .venueId(1L)
                .gradeName(GradeName.R)
                .rowName("A")
                .seatNumber(2)
                .seatStatus(SeatStatus.SOLD)
                .createdAt(LocalDateTime.now())
                .build();

        given(seatService.findOne(eq(1L), eq(2L))).willReturn(response);

        // when & then
        mockMvc.perform(get("/venues/{venueId}/seats/{seatId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.seatId").value(2L))
                .andExpect(jsonPath("$.data.gradeName").value("R"))
                .andExpect(jsonPath("$.data.seatStatus").value("SOLD"))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andDo(restDocsHandler("seat-get-pages"));
    }
}
