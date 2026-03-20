package com.example.ticketingproject.auth.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.auth.dto.LoginRequest;
import com.example.ticketingproject.auth.dto.LoginResponse;
import com.example.ticketingproject.auth.dto.RegisterRequest;
import com.example.ticketingproject.auth.dto.RegisterResponse;
import com.example.ticketingproject.auth.service.AuthService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AuthControllerTest extends RestDocsSupport {

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("회원가입 성공")
    void register_success() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "test@test.com",
                    "name": "홍길동",
                    "password": "password123",
                    "phone": "010-1234-5678"
                }
                """;

        RegisterResponse response = RegisterResponse.builder()
                .id(1L)
                .email("test@test.com")
                .name("홍길동")
                .phone("010-1234-5678")
                .role("USER")
                .status("ACTIVE")
                .build();

        given(authService.register(any(RegisterRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201_REGISTER_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.phone").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andDo(restDocsHandler("auth-register"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 형식 오류")
    void register_fail_invalid_email() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "invalid-email",
                    "name": "홍길동",
                    "password": "password123",
                    "phone": "010-1234-5678"
                }
                """;

        // when & then
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 실패 - 전화번호 형식 오류")
    void register_fail_invalid_phone() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "test@test.com",
                    "name": "홍길동",
                    "password": "password123",
                    "phone": "01012345678"
                }
                """;

        // when & then
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 길이 미달")
    void register_fail_short_password() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "test@test.com",
                    "name": "홍길동",
                    "password": "short",
                    "phone": "010-1234-5678"
                }
                """;

        // when & then
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("관리자 회원가입 성공")
    void admin_register_success() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "admin@test.com",
                    "name": "관리자",
                    "password": "password123",
                    "phone": "010-9999-8888"
                }
                """;

        RegisterResponse response = RegisterResponse.builder()
                .id(2L)
                .email("admin@test.com")
                .name("관리자")
                .phone("010-9999-8888")
                .role("ADMIN")
                .status("ACTIVE")
                .build();

        given(authService.adminRegister(any(RegisterRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/auth/admin-register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201_REGISTER_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(2L))
                .andExpect(jsonPath("$.data.email").value("admin@test.com"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andDo(restDocsHandler("auth-admin-register"));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "test@test.com",
                    "password": "password123"
                }
                """;

        LoginResponse response = new LoginResponse("test-jwt-token");

        given(authService.login(any(LoginRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer test-jwt-token"))
                .andExpect(jsonPath("$.code").value("200_LOGIN_SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").value("test-jwt-token"))
                .andDo(restDocsHandler("auth-login"));
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 형식 오류")
    void login_fail_invalid_email() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "not-an-email",
                    "password": "password123"
                }
                """;

        // when & then
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 미입력")
    void login_fail_blank_password() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "test@test.com",
                    "password": ""
                }
                """;

        // when & then
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() throws Exception {
        // when & then
        mockMvc.perform(post("/auth/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_LOGOUT_SUCCESS"))
                .andDo(restDocsHandler("auth-logout"));
    }
}
