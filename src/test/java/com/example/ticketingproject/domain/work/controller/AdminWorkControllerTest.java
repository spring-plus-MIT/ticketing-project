package com.example.ticketingproject.domain.work.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.work.dto.CreateWorkRequest;
import com.example.ticketingproject.domain.work.dto.UpdateWorkRequest;
import com.example.ticketingproject.domain.work.dto.WorkResponse;
import com.example.ticketingproject.domain.work.service.AdminWorkService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminWorkController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminWorkControllerTest extends RestDocsSupport {

    @MockBean
    private AdminWorkService adminWorkService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("관리자 - 작품 등록 성공")
    @WithMockUser(roles = "ADMIN")
    void create_work_success() throws Exception {
        String requestBody = """
                {
                    "title": "새로운 공연",
                    "category": "MUSICAL",
                    "description": "공연 상세 설명입니다.",
                    "likeCount": 0
                }
                """;

        WorkResponse response = WorkResponse.builder()
                .id(1L)
                .title("새로운 공연")
                .build();

        given(adminWorkService.createWork(any(CreateWorkRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/admin/works")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS"))
                .andDo(restDocsHandler("admin-work-create"));
    }

    @Test
    @DisplayName("관리자 - 작품 수정 성공")
    @WithMockUser(roles = "ADMIN")
    void update_work_success() throws Exception {
        String requestBody = """
                {
                    "title": "수정된 공연 제목",
                    "category": "MUSICAL",
                    "description": "수정된 상세 설명입니다.",
                    "likeCount": 10
                }
                """;

        WorkResponse response = WorkResponse.builder()
                .id(1L)
                .title("수정된 공연 제목")
                .build();

        given(adminWorkService.updateWork(anyLong(), any(UpdateWorkRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(put("/admin/works/{workId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()) // 여기서 400 에러가 났던 것!
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andExpect(jsonPath("$.data.title").value("수정된 공연 제목"))
                .andDo(restDocsHandler("admin-work-update"));
    }
}