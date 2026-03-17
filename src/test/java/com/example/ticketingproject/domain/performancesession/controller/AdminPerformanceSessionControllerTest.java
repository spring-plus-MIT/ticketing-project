package com.example.ticketingproject.domain.performancesession.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.performancesession.dto.SessionRequest;
import com.example.ticketingproject.domain.performancesession.service.AdminPerformanceSessionService;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminPerformanceSessionController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminPerformanceSessionControllerTest extends RestDocsSupport {

    @MockBean
    private AdminPerformanceSessionService adminPerformanceSessionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("관리자 - 공연 회차 등록 성공")
    @WithMockUser(roles = "ADMIN")
    void create_session_success() throws Exception {
        // given
        String requestBody = """
                {
                    "venueId": 1,
                    "startTime": "2026-05-01T14:00:00",
                    "endTime": "2026-05-01T17:00:00"
                }
                """;

        willDoNothing().given(adminPerformanceSessionService).createSession(eq(1L), any(SessionRequest.class));

        // when & then
        mockMvc.perform(post("/admin/performances/{performanceId}/sessions", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated()) // 201 Created 응답 확인
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS"))
                .andDo(restDocsHandler("admin-session-create"));
    }

    @Test
    @DisplayName("관리자 - 공연 회차 수정 성공")
    @WithMockUser(roles = "ADMIN")
    void update_session_success() throws Exception {
        // given
        String requestBody = """
                {
                    "venueId": 2,
                    "startTime": "2026-05-02T19:00:00",
                    "endTime": "2026-05-02T22:00:00"
                }
                """;

        willDoNothing().given(adminPerformanceSessionService).updateSession(eq(100L), any(SessionRequest.class));

        // when & then
        mockMvc.perform(patch("/admin/performances/{performanceId}/sessions/{sessionId}", 1L, 100L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andDo(restDocsHandler("admin-session-update"));
    }

    @Test
    @DisplayName("관리자 - 공연 회차 삭제 성공")
    @WithMockUser(roles = "ADMIN")
    void delete_session_success() throws Exception {
        // given
        willDoNothing().given(adminPerformanceSessionService).deleteSession(100L);

        // when & then
        mockMvc.perform(delete("/admin/performances/{performanceId}/sessions/{sessionId}", 1L, 100L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("admin-session-delete"));
    }
}