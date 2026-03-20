package com.example.ticketingproject.domain.payment.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.payment.dto.PaymentResponse;
import com.example.ticketingproject.domain.payment.enums.PaymentStatus;
import com.example.ticketingproject.domain.payment.service.AdminPaymentService;
import com.example.ticketingproject.domain.payment.service.PaymentService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminPaymentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminPaymentControllerTest extends RestDocsSupport {

    @MockBean
    private AdminPaymentService adminPaymentService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private PaymentResponse samplePayment() {
        return PaymentResponse.builder()
                .id(1L)
                .reservationId(10L)
                .userId(100L)
                .amount(new BigDecimal("50000"))
                .paymentStatus(PaymentStatus.SUCCESS)
                .balanceAfterPayment(new BigDecimal("950000"))
                .build();
    }

    @Test
    @DisplayName("어드민 - 단건 결제 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void 단건_결제_조회_성공() throws Exception {
        // given
        given(paymentService.findOnePayment(eq(1L), eq(100L))).willReturn(samplePayment());

        // when & then
        mockMvc.perform(get("/admin/payments/{paymentId}/{userId}", 1L, 100L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.reservationId").value(10L))
                .andExpect(jsonPath("$.data.userId").value(100L))
                .andExpect(jsonPath("$.data.amount").value(50000))
                .andExpect(jsonPath("$.data.paymentStatus").value("SUCCESS"))
                .andDo(restDocsHandler("admin-payment-get-one"));
    }

    @Test
    @DisplayName("어드민 - 단건 결제 조회 실패 (인증 없음)")
    void 단건_결제_조회_실패_인증없음() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/payments/{paymentId}/{userId}", 1L, 100L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("어드민 - 단건 결제 조회 실패 (USER 권한)")
    @WithMockUser(roles = "USER")
    void 단건_결제_조회_실패_권한없음() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/payments/{paymentId}/{userId}", 1L, 100L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("어드민 - 전체 결제 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void 전체_결제_조회_성공() throws Exception {
        // given
        Page<PaymentResponse> page = new PageImpl<>(
                List.of(samplePayment()),
                PageRequest.of(0, 20),
                1
        );

        given(adminPaymentService.findAllPayments(any())).willReturn(page);

        // when & then
        mockMvc.perform(get("/admin/payments")
                        .with(csrf())
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].paymentStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andDo(restDocsHandler("admin-payment-get-all"));
    }

    @Test
    @DisplayName("어드민 - 전체 결제 조회 실패 (인증 없음)")
    void 전체_결제_조회_실패_인증없음() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("어드민 - 전체 결제 조회 실패 (USER 권한)")
    @WithMockUser(roles = "USER")
    void 전체_결제_조회_실패_권한없음() throws Exception {
        // when & then
        mockMvc.perform(get("/admin/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
