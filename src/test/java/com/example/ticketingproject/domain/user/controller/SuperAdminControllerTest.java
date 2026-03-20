package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.user.dto.UpdateUserResponse;
import com.example.ticketingproject.domain.user.service.UserService;
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

@WebMvcTest(SuperAdminController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class SuperAdminControllerTest extends RestDocsSupport {

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("슈퍼 어드민 - 어드민 활성화 성공")
    @WithMockUser(roles = "SUPER_ADMIN")
    void 어드민_활성화_성공() throws Exception {
        // given
        willDoNothing().given(userService).activateUser(1L);

        // when & then
        mockMvc.perform(post("/super/admin/{adminId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andDo(restDocsHandler("super-admin-activate"));
    }

    @Test
    @DisplayName("슈퍼 어드민 - 어드민 활성화 실패 (인증 없음)")
    void 어드민_활성화_실패_인증없음() throws Exception {
        // when & then
        mockMvc.perform(post("/super/admin/{adminId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("슈퍼 어드민 - 어드민 활성화 실패 (ADMIN 권한)")
    @WithMockUser(roles = "ADMIN")
    void 어드민_활성화_실패_권한없음() throws Exception {
        // when & then
        mockMvc.perform(post("/super/admin/{adminId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("슈퍼 어드민 - 유저 정보 수정 성공")
    @WithMockUser(roles = "SUPER_ADMIN")
    void 유저_정보_수정_성공() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "홍길동",
                    "password": "newpassword1",
                    "phone": "010-9876-5432"
                }
                """;

        UpdateUserResponse response = UpdateUserResponse.builder()
                .id(1L)
                .name("홍길동")
                .phone("010-9876-5432")
                .build();

        given(userService.updateUser(eq(1L), any())).willReturn(response);

        // when & then
        mockMvc.perform(put("/super/admin/{userId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.phone").value("010-9876-5432"))
                .andDo(restDocsHandler("super-admin-update-user"));
    }

    @Test
    @DisplayName("슈퍼 어드민 - 유저 정보 수정 실패 (이름 10자 초과)")
    @WithMockUser(roles = "SUPER_ADMIN")
    void 유저_정보_수정_실패_이름_10자초과() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "이름이열글자를초과합니다",
                    "password": "newpassword1",
                    "phone": "010-9876-5432"
                }
                """;

        // when & then
        mockMvc.perform(put("/super/admin/{userId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("슈퍼 어드민 - 유저 정보 수정 실패 (전화번호 형식 오류)")
    @WithMockUser(roles = "SUPER_ADMIN")
    void 유저_정보_수정_실패_전화번호_형식오류() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "홍길동",
                    "password": "newpassword1",
                    "phone": "01098765432"
                }
                """;

        // when & then
        mockMvc.perform(put("/super/admin/{userId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("슈퍼 어드민 - 유저 정보 수정 실패 (인증 없음)")
    void 유저_정보_수정_실패_인증없음() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "홍길동",
                    "password": "newpassword1",
                    "phone": "010-9876-5432"
                }
                """;

        // when & then
        mockMvc.perform(put("/super/admin/{userId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("슈퍼 어드민 - 유저 탈퇴 처리 성공")
    @WithMockUser(roles = "SUPER_ADMIN")
    void 유저_탈퇴_처리_성공() throws Exception {
        // given
        willDoNothing().given(userService).withdrawUser(1L);

        // when & then
        mockMvc.perform(delete("/super/admin/{userId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("super-admin-withdraw-user"));
    }

    @Test
    @DisplayName("슈퍼 어드민 - 유저 탈퇴 처리 실패 (인증 없음)")
    void 유저_탈퇴_처리_실패_인증없음() throws Exception {
        // when & then
        mockMvc.perform(delete("/super/admin/{userId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("슈퍼 어드민 - 유저 탈퇴 처리 실패 (ADMIN 권한)")
    @WithMockUser(roles = "ADMIN")
    void 유저_탈퇴_처리_실패_권한없음() throws Exception {
        // when & then
        mockMvc.perform(delete("/super/admin/{userId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
