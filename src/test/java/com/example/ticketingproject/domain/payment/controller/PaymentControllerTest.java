package com.example.ticketingproject.domain.payment.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.payment.dto.PaymentResponse;
import com.example.ticketingproject.domain.payment.enums.PaymentStatus;
import com.example.ticketingproject.domain.payment.service.PaymentService;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.security.CustomUserDetails;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class PaymentControllerTest extends RestDocsSupport {

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private CustomUserDetails mockUser() {
        return new CustomUserDetails(1L, "test@test.com", UserRole.USER);
    }

    private PaymentResponse samplePayment() {
        return PaymentResponse.builder()
                .id(1L)
                .reservationId(10L)
                .userId(1L)
                .amount(new BigDecimal("50000"))
                .paymentStatus(PaymentStatus.SUCCESS)
                .balanceAfterPayment(new BigDecimal("950000"))
                .build();
    }

    @Test
    @DisplayName("결제 생성 성공")
    void 결제_생성_성공() throws Exception {
        // given
        String requestBody = """
                {
                    "reservationId": 10,
                    "amount": 50000
                }
                """;

        given(paymentService.createPayment(any(), eq(1L))).willReturn(samplePayment());

        // when & then
        mockMvc.perform(post("/payments")
                        .with(csrf())
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.reservationId").value(10L))
                .andExpect(jsonPath("$.data.amount").value(50000))
                .andExpect(jsonPath("$.data.paymentStatus").value("SUCCESS"))
                .andDo(restDocsHandler("payment-create"));
    }

    @Test
    @DisplayName("결제 생성 실패 (reservationId 누락)")
    void 결제_생성_실패_reservationId_누락() throws Exception {
        // given
        String requestBody = """
                {
                    "amount": 50000
                }
                """;

        // when & then
        mockMvc.perform(post("/payments")
                        .with(csrf())
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("결제 생성 실패 (amount 음수)")
    void 결제_생성_실패_amount_음수() throws Exception {
        // given
        String requestBody = """
                {
                    "reservationId": 10,
                    "amount": -1000
                }
                """;

        // when & then
        mockMvc.perform(post("/payments")
                        .with(csrf())
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("결제 생성 실패 (인증 없음)")
    void 결제_생성_실패_인증없음() throws Exception {
        // given
        String requestBody = """
                {
                    "reservationId": 10,
                    "amount": 50000
                }
                """;

        // when & then
        mockMvc.perform(post("/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("단건 결제 조회 성공")
    void 단건_결제_조회_성공() throws Exception {
        // given
        given(paymentService.findOnePayment(eq(1L), eq(1L))).willReturn(samplePayment());

        // when & then
        mockMvc.perform(get("/payments/{paymentId}", 1L)
                        .with(csrf())
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.reservationId").value(10L))
                .andExpect(jsonPath("$.data.paymentStatus").value("SUCCESS"))
                .andDo(restDocsHandler("payment-get-one"));
    }

    @Test
    @DisplayName("단건 결제 조회 실패 (인증 없음)")
    void 단건_결제_조회_실패_인증없음() throws Exception {
        // when & then
        mockMvc.perform(get("/payments/{paymentId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("전체 결제 조회 성공")
    void 전체_결제_조회_성공() throws Exception {
        // given
        Page<PaymentResponse> page = new PageImpl<>(
                List.of(samplePayment()),
                PageRequest.of(0, 20),
                1
        );

        given(paymentService.findAllPayments(eq(1L), any())).willReturn(page);

        // when & then
        mockMvc.perform(get("/payments")
                        .with(csrf())
                        .with(user(mockUser()))
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].paymentStatus").value("SUCCESS"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andDo(restDocsHandler("payment-get-all"));
    }

    @Test
    @DisplayName("전체 결제 조회 실패 (인증 없음)")
    void 전체_결제_조회_실패_인증없음() throws Exception {
        // when & then
        mockMvc.perform(get("/payments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
