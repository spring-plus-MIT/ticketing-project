package com.example.ticketingproject.domain.castmember.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.castmember.dto.CastMemberRequest;
import com.example.ticketingproject.domain.castmember.service.AdminCastMemberService;
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

@WebMvcTest(AdminCastMemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminCastMemberControllerTest extends RestDocsSupport {

    @MockBean
    private AdminCastMemberService adminCastMemberService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("관리자 - 출연진 등록 성공")
    @WithMockUser(roles = "ADMIN")
    void create_cast_success() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "홍길동",
                    "roleName": "주연"
                }
                """;

        willDoNothing().given(adminCastMemberService).createCastMember(eq(1L), any(CastMemberRequest.class));

        // when & then
        mockMvc.perform(post("/admin/performances/{performanceId}/sessions/{sessionId}/casts", 1L, 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS")) // 201 확인!
                .andDo(restDocsHandler("admin-cast-create"));
    }

    @Test
    @DisplayName("관리자 - 출연진 수정 성공")
    @WithMockUser(roles = "ADMIN")
    void update_cast_success() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "임꺽정",
                    "roleName": "조연"
                }
                """;

        willDoNothing().given(adminCastMemberService).updateCastMember(eq(100L), any(CastMemberRequest.class));

        // when & then
        mockMvc.perform(patch("/admin/performances/1/sessions/1/casts/{castId}", 100L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andDo(restDocsHandler("admin-cast-update"));
    }

    @Test
    @DisplayName("관리자 - 출연진 삭제 성공")
    @WithMockUser(roles = "ADMIN")
    void delete_cast_success() throws Exception {
        // given
        willDoNothing().given(adminCastMemberService).deleteCastMember(100L);

        // when & then
        mockMvc.perform(delete("/admin/performances/1/sessions/1/casts/{castId}", 100L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("admin-cast-delete"));
    }
}