package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("관리자 회원가입 성공 - HTTP 200, code 201_REGISTER_SUCCESS, status PENDING으로 저장")
    void admin_register_success_with_pending_status() throws Exception {
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

    @Test
    void updateUser() {
    }

    @Test
    void withdrawUser() {
    }
}
