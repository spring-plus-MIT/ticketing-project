package com.example.ticketingproject.domain.work.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.work.dto.WorkResponse;
import com.example.ticketingproject.domain.work.service.WorkService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class WorkControllerTest extends RestDocsSupport {

    @MockBean
    private WorkService workService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("사용자 - 작품 목록 페이징 조회 성공")
    @WithMockUser
    void get_works_success() throws Exception {
        // given
        WorkResponse response = WorkResponse.builder()
                .id(1L)
                .title("뮤지컬 레미제라블")
                .description("설명")
                .build();

        PageImpl<WorkResponse> pageResponse = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 10),
                1
        );

        given(workService.findAllWork(any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/works")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].title").value("뮤지컬 레미제라블"))
                .andDo(restDocsHandler("work-get-list"));
    }

    @Test
    @DisplayName("사용자 - 작품 단건 상세 조회 성공")
    @WithMockUser
    void get_one_work_success() throws Exception {
        // given
        WorkResponse response = WorkResponse.builder()
                .id(1L)
                .title("뮤지컬 레미제라블")
                .description("상세 설명")
                .build();

        given(workService.findOneWork(anyLong())).willReturn(response);

        // when & then
        mockMvc.perform(get("/works/{workId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.title").value("뮤지컬 레미제라블"))
                .andDo(restDocsHandler("work-get-one"));
    }
}