package com.example.ticketingproject.common.search.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.common.search.dto.PerformanceSearchResponse;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.service.PerformanceSessionService;
import com.example.ticketingproject.domain.work.enums.Category;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PerformanceSearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class PerformanceSearchControllerTest extends RestDocsSupport {

    @MockBean
    private PerformanceSessionService performanceSessionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private PerformanceSearchResponse createSampleResponse() {
        return new PerformanceSearchResponse(
                1L,
                "레미제라블 시즌 1",
                Category.MUSICAL,
                PerformanceStatus.ON_SALE,
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 3, 31, 22, 0)
        );
    }

    @Test
    @DisplayName("공연 검색")
    @WithMockUser
    void search_v2_with_login_success() throws Exception {
        // given
        PageImpl<PerformanceSearchResponse> pageResponse = new PageImpl<>(
                List.of(createSampleResponse()),
                PageRequest.of(0, 10),
                1
        );

        given(performanceSessionService.searchV2(any(), any(), any(), any(), any(), any(), any()))
                .willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/performance/search/v2")
                        .param("keyword", "레미제라블")
                        .param("category", "MUSICAL")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-03-31")
                        .param("status", "ON_SALE")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].performanceId").value(1L))
                .andExpect(jsonPath("$.data.content[0].season").value("레미제라블 시즌 1"))
                .andExpect(jsonPath("$.data.content[0].category").value("MUSICAL"))
                .andExpect(jsonPath("$.data.content[0].status").value("ON_SALE"))
                .andExpect(jsonPath("$.data.content[0].startDate").exists())
                .andExpect(jsonPath("$.data.content[0].endDate").exists())
                .andDo(restDocsHandler("performance-search-v2-"));
    }
}