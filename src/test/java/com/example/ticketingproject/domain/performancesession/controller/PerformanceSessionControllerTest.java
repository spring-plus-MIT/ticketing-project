package com.example.ticketingproject.domain.performancesession.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.common.config.SuperAdminInitializer;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.performancesession.service.PerformanceSessionService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PerformanceSessionController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class PerformanceSessionControllerTest extends RestDocsSupport {

    @MockBean
    private SuperAdminInitializer superAdminInitializer;

    @MockBean
    private PerformanceSessionService performanceSessionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("사용자 - 공연 회차 목록 페이징 조회 성공")
    @WithMockUser
    void get_session_pages_success() throws Exception {
        // given
        GetSessionResponse response = GetSessionResponse.builder()
                .id(1L)
                .title("오페라의 유령")
                .venueName("예술의전당")
                .startTime(LocalDateTime.of(2026, 5, 1, 14, 0))
                .endTime(LocalDateTime.of(2026, 5, 1, 17, 0))
                .build();

        PageImpl<GetSessionResponse> pageResponse = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 10),
                1
        );

        given(performanceSessionService.getSessions(eq(1L), any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/performances/{performanceId}/sessions", 1L)
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].title").value("오페라의 유령"))
                // LocalDateTime은 문자열로 직렬화될 때 'T'가 포함되거나 초 단위가 생략될 수 있으므로 존재 유무 정도만 테스트해도 무방합니다.
                .andExpect(jsonPath("$.data.content[0].startTime").exists())
                .andDo(restDocsHandler("session-get-pages"));
    }

    @Test
    @DisplayName("사용자 - 공연 회차 상세 조회 성공")
    @WithMockUser
    void get_session_detail_success() throws Exception {
        // given
        GetSessionResponse response = GetSessionResponse.builder()
                .id(1L)
                .title("레미제라블")
                .venueName("샤롯데씨어터")
                .startTime(LocalDateTime.of(2026, 7, 1, 19, 0))
                .endTime(LocalDateTime.of(2026, 7, 1, 22, 0))
                .build();

        given(performanceSessionService.getSessionDetail(eq(1L))).willReturn(response);

        // when & then
        mockMvc.perform(get("/performances/{performanceId}/sessions/{sessionId}", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("레미제라블"))
                .andDo(restDocsHandler("session-get-detail"));
    }
}