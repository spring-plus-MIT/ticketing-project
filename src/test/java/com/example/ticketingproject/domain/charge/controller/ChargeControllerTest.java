package com.example.ticketingproject.domain.charge.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.charge.dto.ChargeResponse;
import com.example.ticketingproject.domain.charge.service.ChargeService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChargeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class ChargeControllerTest extends RestDocsSupport {

    @MockBean
    private ChargeService chargeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private CustomUserDetails mockUser() {
        return new CustomUserDetails(1L, "user@test.com", UserRole.USER);
    }

    private ChargeResponse createSampleResponse() {
        return ChargeResponse.builder()
                .chargeId(1L)
                .userId(1L)
                .adminId(2L)
                .amount(BigDecimal.valueOf(10000))
                .balanceAfterCharge(BigDecimal.valueOf(50000))
                .build();
    }

    @Test
    @DisplayName("유저 - 본인 충전 내역 조회 성공")
    void find_all_charges_success() throws Exception {
        // given
        PageImpl<ChargeResponse> pageResponse = new PageImpl<>(
                List.of(createSampleResponse()),
                PageRequest.of(0, 10),
                1
        );

        given(chargeService.findAll(eq(1L), any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/charges")
                        .with(user(mockUser()))
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].chargeId").value(1L))
                .andExpect(jsonPath("$.data.content[0].userId").value(1L))
                .andExpect(jsonPath("$.data.content[0].amount").value(10000))
                .andExpect(jsonPath("$.data.content[0].balanceAfterCharge").value(50000))
                .andDo(restDocsHandler("charge-find-all"));
    }
}