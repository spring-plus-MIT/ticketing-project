package com.example.ticketingproject.domain.performance.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.common.config.SuperAdminInitializer;
import com.example.ticketingproject.domain.performance.dto.PerformanceResponse;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performance.service.PerformanceService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PerformanceController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class PerformanceControllerTest extends RestDocsSupport {

    @MockBean
    private SuperAdminInitializer superAdminInitializer;

    @MockBean
    private PerformanceService performanceService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("사용자 - 공연 목록 페이징 조회 성공")
    @WithMockUser
    void get_performance_pages_success() throws Exception {
        // given
        PerformanceResponse response = PerformanceResponse.builder()
                .id(1L)
                .workTitle("오페라의 유령")
                .venueName("예술의전당")
                .season("2026 내한공연")
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 6, 30))
                .status(PerformanceStatus.UPCOMING)
                .build();

        PageImpl<PerformanceResponse> pageResponse = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 10),
                1
        );

        given(performanceService.getPerformances(any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/performances")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].workTitle").value("오페라의 유령"))
                .andExpect(jsonPath("$.data.content[0].status").value("UPCOMING"))
                .andDo(restDocsHandler("performance-get-pages"));
    }

    @Test
    @DisplayName("사용자 - 공연 단건 상세 조회 성공")
    @WithMockUser
    void get_performance_detail_success() throws Exception {
        // given
        PerformanceResponse response = PerformanceResponse.builder()
                .id(1L)
                .workTitle("레미제라블")
                .venueName("샤롯데씨어터")
                .season("2026 앙코르")
                .startDate(LocalDate.of(2026, 7, 1))
                .endDate(LocalDate.of(2026, 8, 31))
                .status(PerformanceStatus.ON_SALE)
                .build();

        given(performanceService.getPerformanceDetail(eq(1L))).willReturn(response);

        // when & then
        mockMvc.perform(get("/performances/{performanceId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.workTitle").value("레미제라블"))
                .andExpect(jsonPath("$.data.venueName").value("샤롯데씨어터"))
                .andDo(restDocsHandler("performance-get-detail"));
    }
}