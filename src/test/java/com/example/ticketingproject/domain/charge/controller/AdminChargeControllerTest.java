package com.example.ticketingproject.domain.charge.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.charge.dto.ChargeRequest;
import com.example.ticketingproject.domain.charge.dto.ChargeResponse;
import com.example.ticketingproject.domain.charge.service.AdminChargeService;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.security.CustomUserDetails;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminChargeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminChargeControllerTest extends RestDocsSupport {

    @MockBean
    private AdminChargeService adminChargeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private ChargeRequest request;

    private CustomUserDetails mockUser() {
        return new CustomUserDetails(1L, "test@test.com", UserRole.ADMIN);
    }

    private ChargeResponse createSampleResponse() {
        return ChargeResponse.builder()
                .chargeId(1L)
                .userId(2L)
                .adminId(1L)
                .amount(BigDecimal.valueOf(10000))
                .balanceAfterCharge(BigDecimal.valueOf(50000))
                .build();
    }

    @Test
    @DisplayName("관리자 - 유저 잔액 충전 성공")
    void charge_success() throws Exception {
        // given
        request = new ChargeRequest();
        ReflectionTestUtils.setField(request, "amount", BigDecimal.valueOf(10000));

        given(adminChargeService.charge(anyLong(), eq(2L), any()))
                .willReturn(createSampleResponse());

        // when & then
        mockMvc.perform(post("/admin/charges/{userId}", 2L)
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200_CHARGE_SUCCESS"))
                .andExpect(jsonPath("$.data.chargeId").value(1L))
                .andExpect(jsonPath("$.data.userId").value(2L))
                .andExpect(jsonPath("$.data.amount").value(10000))
                .andExpect(jsonPath("$.data.balanceAfterCharge").value(50000))
                .andDo(restDocsHandler("admin-charge"));
    }

    @Test
    @DisplayName("관리자 - 전체 충전 내역 조회 성공")
    void get_all_charges_success() throws Exception {
        // given
        PageImpl<ChargeResponse> pageResponse = new PageImpl<>(
                List.of(createSampleResponse()),
                PageRequest.of(0, 10),
                1
        );

        given(adminChargeService.findAll(any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/admin/charges")
                        .with(user(mockUser()))
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].chargeId").value(1L))
                .andExpect(jsonPath("$.data.content[0].userId").value(2L))
                .andExpect(jsonPath("$.data.content[0].amount").value(10000))
                .andDo(restDocsHandler("admin-charge-get-all"));
    }

    @Test
    @DisplayName("관리자 - 특정 유저 충전 내역 조회 성공")
    void get_all_charges_by_user_id_success() throws Exception {
        // given
        PageImpl<ChargeResponse> pageResponse = new PageImpl<>(
                List.of(createSampleResponse()),
                PageRequest.of(0, 10),
                1
        );

        given(adminChargeService.findAllByUserId(eq(2L), any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/admin/charges/{userId}", 2L)
                        .with(user(mockUser()))
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].chargeId").value(1L))
                .andExpect(jsonPath("$.data.content[0].userId").value(2L))
                .andExpect(jsonPath("$.data.content[0].amount").value(10000))
                .andDo(restDocsHandler("admin-charge-get-all-by-user-id"));
    }
}