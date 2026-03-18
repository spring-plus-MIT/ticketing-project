package com.example.ticketingproject.domain.user.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.common.config.SuperAdminInitializer;
import com.example.ticketingproject.domain.user.dto.GetUserResponse;
import com.example.ticketingproject.domain.user.dto.UpdateUserResponse;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.service.AdminUserService;
import com.example.ticketingproject.domain.user.service.UserService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminUserControllerTest extends RestDocsSupport {

    @MockBean
    private SuperAdminInitializer superAdminInitializer;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    void 전체_유저_조회_성공() throws Exception {
        // given
        GetUserResponse userResponse = GetUserResponse.builder()
                .id(1L)
                .name("테스트유저")
                .email("test@test.com")
                .phone("010-1234-5678")
                .balance(BigDecimal.valueOf(10000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        Page<GetUserResponse> page = new PageImpl<>(List.of(userResponse), PageRequest.of(0, 10), 1);
        given(adminUserService.findAllUser(any())).willReturn(page);

        // when & then
        mockMvc.perform(get("/admin/users")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].email").value("test@test.com"))
                .andDo(restDocsHandler("admin-users-get"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 유저_정보_수정_성공() throws Exception {
        // given
        UpdateUserResponse updateResponse = UpdateUserResponse.builder()
                .id(1L)
                .name("수정이름")
                .phone("010-5678-5678")
                .build();

        given(userService.updateUser(eq(1L), any())).willReturn(updateResponse);

        String requestBody = """
                {
                    "name": "수정이름",
                    "password": "newpassword123",
                    "phone": "010-5678-5678"
                }
                """;

        // when & then
        mockMvc.perform(put("/admin/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("수정이름"))
                .andExpect(jsonPath("$.data.phone").value("010-5678-5678"))
                .andDo(restDocsHandler("admin-users-update"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 유저_정보_수정_실패_유효성_검사_이름_공백() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "",
                    "password": "newpassword123",
                    "phone": "010-5678-5678"
                }
                """;

        // when & then
        mockMvc.perform(put("/admin/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 유저_정보_수정_실패_유효성_검사_비밀번호_짧음() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "수정이름",
                    "password": "short",
                    "phone": "010-5678-5678"
                }
                """;

        // when & then
        mockMvc.perform(put("/admin/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 유저_정보_수정_실패_유효성_검사_전화번호_형식_오류() throws Exception {
        // given
        String requestBody = """
                {
                    "name": "수정이름",
                    "password": "newpassword123",
                    "phone": "01012345678"
                }
                """;

        // when & then
        mockMvc.perform(put("/admin/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 유저_탈퇴_성공() throws Exception {
        // given
        willDoNothing().given(userService).withdrawUser(1L);

        // when & then
        mockMvc.perform(delete("/admin/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("admin-users-delete"));
    }

    @Test
    void 인증_없이_전체_유저_조회_실패() throws Exception {
        // Spring Security 6에서 authenticationEntryPoint 미설정 시 403 반환
        // when & then
        mockMvc.perform(get("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void ADMIN_권한_없이_전체_유저_조회_실패() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
