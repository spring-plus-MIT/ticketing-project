package com.example.ticketingproject.domain.performance.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.common.config.SuperAdminInitializer;
import com.example.ticketingproject.domain.performance.dto.PerformanceRequest;
import com.example.ticketingproject.domain.performance.service.AdminPerformanceService;
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

@WebMvcTest(AdminPerformanceController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminPerformanceControllerTest extends RestDocsSupport {

    @MockBean
    private SuperAdminInitializer superAdminInitializer;

    @MockBean
    private AdminPerformanceService adminPerformanceService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("관리자 - 공연 등록 성공")
    @WithMockUser(roles = "ADMIN")
    void create_performance_success() throws Exception {
        // given
        String requestBody = """
                {
                    "workId": 1,
                    "venueId": 1,
                    "season": "2026 시즌",
                    "startDate": "2026-05-01",
                    "endDate": "2026-06-30",
                    "status": "UPCOMING"
                }
                """;

        willDoNothing().given(adminPerformanceService).createPerformance(any(PerformanceRequest.class));

        // when & then
        mockMvc.perform(post("/admin/performances")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated()) // HttpStatus.CREATED (201) 기대
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS"))
                .andDo(restDocsHandler("admin-performance-create"));
    }

    @Test
    @DisplayName("관리자 - 공연 수정 성공")
    @WithMockUser(roles = "ADMIN")
    void update_performance_success() throws Exception {
        // given
        String requestBody = """
                {
                    "workId": 1,
                    "venueId": 2,
                    "season": "2026 시즌 변경",
                    "startDate": "2026-06-01",
                    "endDate": "2026-07-31",
                    "status": "ON_SALE"
                }
                """;

        willDoNothing().given(adminPerformanceService).updatePerformance(eq(1L), any(PerformanceRequest.class));

        // when & then
        mockMvc.perform(patch("/admin/performances/{performanceId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andDo(restDocsHandler("admin-performance-update"));
    }

    @Test
    @DisplayName("관리자 - 공연 조기 종료(삭제 처리) 성공")
    @WithMockUser(roles = "ADMIN")
    void close_performance_success() throws Exception {
        // given
        willDoNothing().given(adminPerformanceService).closePerformance(1L);

        // when & then
        mockMvc.perform(delete("/admin/performances/{performanceId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("admin-performance-delete"));
    }
}