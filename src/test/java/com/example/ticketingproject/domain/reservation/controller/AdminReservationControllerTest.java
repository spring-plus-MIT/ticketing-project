package com.example.ticketingproject.domain.reservation.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.reservation.dto.response.ReservationResponse;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.service.AdminReservationService;
import com.example.ticketingproject.domain.reservation.service.ReservationService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminReservationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminReservationControllerTest extends RestDocsSupport {

    @MockBean
    private AdminReservationService adminReservationService;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("관리자 - 전체 예매 내역 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void get_all_reservations_success() throws Exception {
        // given
        ReservationResponse response = createMockReservationResponse();
        Page<ReservationResponse> pageResponse = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        given(adminReservationService.getAllReservations(any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/admin/reservations")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].performanceTitle").value("오페라의 유령"))
                .andDo(restDocsHandler("admin-reservation-get-all"));
    }

    @Test
    @DisplayName("관리자 - 특정 사용자 예매 내역 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void get_reservations_by_user_success() throws Exception {
        // given
        Long userId = 100L;
        ReservationResponse response = createMockReservationResponse();
        Page<ReservationResponse> pageResponse = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        given(adminReservationService.getReservationsByUser(eq(userId), any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/admin/reservations/users/{userId}", userId)
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].userId").value(100L))
                .andDo(restDocsHandler("admin-reservation-get-by-user"));
    }

    @Test
    @DisplayName("관리자 - 예매 단건 상세 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void get_one_reservation_success() throws Exception {
        // given
        Long userId = 100L;
        Long reservationId = 1L;
        ReservationResponse response = createMockReservationResponse();

        given(adminReservationService.getOneReservation(reservationId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/reservations/{userId}/{reservationId}", userId, reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(reservationId))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andDo(restDocsHandler("admin-reservation-get-one"));
    }

    @Test
    @DisplayName("관리자 - 예매 취소(삭제 처리) 성공")
    @WithMockUser(roles = "ADMIN")
    void cancel_reservation_success() throws Exception {
        // given
        Long reservationId = 1L;
        Long userId = 100L;

        willDoNothing().given(reservationService).cancelReservation(reservationId, userId);

        // when & then
        mockMvc.perform(delete("/admin/reservations/{reservationId}/{userId}", reservationId, userId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("admin-reservation-cancel"));
    }

    private ReservationResponse createMockReservationResponse() {
        return ReservationResponse.builder()
                .id(1L)
                .userId(100L)
                .performanceTitle("오페라의 유령")
                .performanceDate(LocalDate.of(2026, 5, 1))
                .startTime(LocalDateTime.of(2026, 5, 1, 19, 0))
                .seatInfo("VIP - A - 12")
                .totalPrice(BigDecimal.valueOf(150000))
                .status(ReservationStatus.CONFIRMED)
                .build();
    }
}