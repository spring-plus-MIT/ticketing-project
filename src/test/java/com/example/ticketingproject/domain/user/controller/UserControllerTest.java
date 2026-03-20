package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.dto.UpdateUserResponse;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.service.UserService;
import com.example.ticketingproject.security.CustomUserDetails;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class UserControllerTest extends RestDocsSupport {

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private CustomUserDetails mockUser() {
        return new CustomUserDetails(1L, "test@test.com", UserRole.USER);
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void 내_정보_조회_성공() throws Exception {
        // given
        GetUserResponse response = GetUserResponse.builder()
                .id(1L)
                .name("홍길동")
                .email("test@test.com")
                .phone("010-1234-5678")
                .balance(BigDecimal.ZERO)
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        given(userService.findOneUser(1L)).willReturn(response);

        // when & then
        mockMvc.perform(get("/users/me")
                        .with(csrf())
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.phone").value("010-1234-5678"))
                .andDo(restDocsHandler("user-get-me"));
    }

    @Test
    @DisplayName("내 정보 조회 실패 (인증 없음)")
    void 내_정보_조회_실패_인증없음() throws Exception {
        // when & then
        mockMvc.perform(get("/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("내 정보 수정 성공")
    void 내_정보_수정_성공() throws Exception {
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
        mockMvc.perform(put("/users/me")
                        .with(csrf())
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.phone").value("010-9876-5432"))
                .andDo(restDocsHandler("user-update-me"));
    }

    @Test
    @DisplayName("내 정보 수정 실패 (이름 10자 초과)")
    void 내_정보_수정_실패_이름_10자초과() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "이름이열글자를초과합니다",
                    "password": "newpassword1",
                    "phone": "010-9876-5432"
                }
                """;

        // when & then
        mockMvc.perform(put("/users/me")
                        .with(csrf())
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("내 정보 수정 실패 (전화번호 형식 오류)")
    void 내_정보_수정_실패_전화번호_형식오류() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "홍길동",
                    "password": "newpassword1",
                    "phone": "01098765432"
                }
                """;

        // when & then
        mockMvc.perform(put("/users/me")
                        .with(csrf())
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("내 정보 수정 실패 (인증 없음)")
    void 내_정보_수정_실패_인증없음() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "홍길동",
                    "password": "newpassword1",
                    "phone": "010-9876-5432"
                }
                """;

        // when & then
        mockMvc.perform(put("/users/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void 회원_탈퇴_성공() throws Exception {
        // given
        willDoNothing().given(userService).withdrawUser(1L);

        // when & then
        mockMvc.perform(delete("/users/delete")
                        .with(csrf())
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("user-withdraw"));
    }

    @Test
    @DisplayName("회원 탈퇴 실패 (인증 없음)")
    void 회원_탈퇴_실패_인증없음() throws Exception {
        // when & then
        mockMvc.perform(delete("/users/delete")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
