package com.example.ticketingproject.domain.reservation.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.reservation.dto.request.ReservationCreateRequest;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.service.ReservationService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class ReservationControllerTest extends RestDocsSupport {

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("사용자 - 예매 생성 성공")
    void create_reservation_success() throws Exception {
        // given
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        given(customUserDetails.getId()).willReturn(100L);

        String requestBody = """
                {
                    "performanceSessionId": 1,
                    "seatId": 50
                }
                """;

        ReservationResponse response = createMockReservationResponse();

        given(reservationService.createReservation(any(ReservationCreateRequest.class), eq(100L)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/reservations")
                        .with(user(customUserDetails)) // CustomUserDetails 주입
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()) // 참고: 컨트롤러에서 ResponseEntity.ok()를 반환하므로 200 OK 기대
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS")) // 코드 자체는 201_CREATE_SUCCESS
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(restDocsHandler("reservation-create"));
    }

    @Test
    @DisplayName("사용자 - 본인 예매 단건 상세 조회 성공")
    void get_one_reservation_success() throws Exception {
        // given
        Long reservationId = 1L;
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        given(customUserDetails.getId()).willReturn(100L);

        ReservationResponse response = createMockReservationResponse();

        given(reservationService.findOneReservation(eq(100L), eq(reservationId))).willReturn(response);

        // when & then
        mockMvc.perform(get("/reservations/{reservationId}", reservationId)
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(reservationId))
                .andDo(restDocsHandler("reservation-get-one"));
    }

    @Test
    @DisplayName("사용자 - 예매 취소 성공")
    void cancel_reservation_success() throws Exception {
        // given
        Long reservationId = 1L;
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        given(customUserDetails.getId()).willReturn(100L);

        willDoNothing().given(reservationService).cancelReservation(eq(reservationId), eq(100L));

        // when & then
        mockMvc.perform(delete("/reservations/{reservationId}", reservationId)
                        .with(user(customUserDetails))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("reservation-cancel"));
    }

    // 테스트용 Mock 데이터 생성 헬퍼 메서드
    private ReservationResponse createMockReservationResponse() {
        return ReservationResponse.builder()
                .id(1L)
                .userId(100L)
                .performanceTitle("오페라의 유령")
                .performanceDate(LocalDate.of(2026, 5, 1))
                .startTime(LocalDateTime.of(2026, 5, 1, 19, 0))
                .seatInfo("VIP - A - 12")
                .totalPrice(BigDecimal.valueOf(150000))
                .status(ReservationStatus.PENDING) // 결제 대기 상태
                .build();
    }
}