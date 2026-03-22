package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.security.CustomUserDetails;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SuperAdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    private String createBearerToken(Long id, String email, UserRole role) {
        CustomUserDetails userDetails = new CustomUserDetails(id, email, role);
        return "Bearer " + jwtTokenProvider.createToken(userDetails);
    }

    private User saveUser(String email, UserRole role, UserStatus status) {
        return userRepository.save(User.builder()
                .name("테스트유저")
                .email(email)
                .password("encodedPassword")
                .phone("010-1234-5678")
                .balance(BigDecimal.ZERO)
                .userRole(role)
                .userStatus(status)
                .build());
    }

    // ===================== 관리자 회원가입 =====================

    @Test
    @DisplayName("관리자 회원가입 성공 - HTTP 200, code 201_REGISTER_SUCCESS, status PENDING으로 저장")
    void PENDING으로_관리자_회원가입() throws Exception {
        // given
        String requestBody = """
                {
                    "email": "admin@test.com",
                    "name": "관리자",
                    "password": "password123",
                    "phone": "010-9999-8888"
                }
                """;

        // when & then - HTTP 응답 검증
        mockMvc.perform(post("/auth/admin-register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("201_REGISTER_SUCCESS"))
                .andExpect(jsonPath("$.data.email").value("admin@test.com"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        // DB 직접 검증
        User savedUser = userRepository.findByEmail("admin@test.com").orElseThrow();
        assertThat(savedUser.getUserRole()).isEqualTo(UserRole.ADMIN);
        assertThat(savedUser.getUserStatus()).isEqualTo(UserStatus.PENDING);
    }

    // ===================== 관리자 활성화 =====================

    @Test
    @DisplayName("일반 관리자 활성화 성공 - HTTP 200, code 200_UPDATE_SUCCESS, status ACTIVE로 변경")
    void 일반_관리자_활성화_성공() throws Exception {
        // given
        User admin = saveUser("admin@test.com", UserRole.ADMIN, UserStatus.PENDING);
        String token = createBearerToken(999L, "super@test.com", UserRole.SUPER_ADMIN);

        // when & then - HTTP 응답 검증
        mockMvc.perform(post("/super/admin/" + admin.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"));

        // DB 직접 검증
        User activatedAdmin = userRepository.findById(admin.getId()).orElseThrow();
        assertThat(activatedAdmin.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("일반 관리자 활성화 실패 - 권한 없음 (ADMIN 역할 토큰으로 요청 시 403)")
    void 일반_관리자_활성화_실패_권한_없음() throws Exception {
        // given
        User admin = saveUser("admin@test.com", UserRole.ADMIN, UserStatus.PENDING);
        String token = createBearerToken(admin.getId(), "admin@test.com", UserRole.ADMIN);

        // when & then
        mockMvc.perform(post("/super/admin/" + admin.getId())
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    // ===================== 고객 정보 수정 =====================

    @Test
    @DisplayName("고객 정보 수정 성공 - HTTP 200, code 200_UPDATE_SUCCESS, 변경된 name/phone 반환")
    void 고객_정보_수정_성공() throws Exception {
        // given
        User user = saveUser("user@test.com", UserRole.USER, UserStatus.ACTIVE);
        String token = createBearerToken(999L, "super@test.com", UserRole.SUPER_ADMIN);

        String requestBody = """
                {
                    "name": "수정이름",
                    "password": "newpassword123",
                    "phone": "010-1111-2222"
                }
                """;

        // when & then
        mockMvc.perform(put("/super/admin/" + user.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andExpect(jsonPath("$.data.name").value("수정이름"))
                .andExpect(jsonPath("$.data.phone").value("010-1111-2222"));
    }

    @Test
    @DisplayName("고객 정보 수정 실패 - 유효성 검사 오류 (잘못된 전화번호 형식으로 요청 시 400)")
    void 고객_정보_수정_실패() throws Exception {
        // given
        User user = saveUser("user@test.com", UserRole.USER, UserStatus.ACTIVE);
        String token = createBearerToken(999L, "super@test.com", UserRole.SUPER_ADMIN);

        String requestBody = """
                {
                    "name": "수정이름",
                    "password": "newpassword123",
                    "phone": "01011112222"
                }
                """;

        // when & then
        mockMvc.perform(put("/super/admin/" + user.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ===================== 고객 삭제 =====================

    @Test
    @DisplayName("고객 삭제 성공 - HTTP 200, code 200_DELETE_SUCCESS, status DELETED로 변경")
    void 고객_삭제_성공() throws Exception {
        // given
        User user = saveUser("user@test.com", UserRole.USER, UserStatus.ACTIVE);
        String token = createBearerToken(999L, "super@test.com", UserRole.SUPER_ADMIN);

        // when & then - HTTP 응답 검증
        mockMvc.perform(delete("/super/admin/" + user.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"));

        // DB 직접 검증
        User deletedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(deletedUser.getUserStatus()).isEqualTo(UserStatus.DELETED);
    }

    @Test
    @DisplayName("고객 삭제 실패 - 권한 없음 (USER 역할 토큰으로 요청 시 403)")
    void 고객_삭제_실패_권한_없음() throws Exception {
        // given
        User user = saveUser("user@test.com", UserRole.USER, UserStatus.ACTIVE);
        String token = createBearerToken(user.getId(), "user@test.com", UserRole.USER);

        // when & then
        mockMvc.perform(delete("/super/admin/" + user.getId())
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("고객 삭제 실패 - 존재하지 않는 유저 (404)")
    void 고객_삭제_실패_없는_유저() throws Exception {
        // given
        String token = createBearerToken(999L, "super@test.com", UserRole.SUPER_ADMIN);
        long nonExistentUserId = 99999L;

        // when & then
        mockMvc.perform(delete("/super/admin/" + nonExistentUserId)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
