package com.example.ticketingproject.domain.seat.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.seat.dto.CreateSeatRequest;
import com.example.ticketingproject.domain.seat.dto.SeatResponse;
import com.example.ticketingproject.domain.seat.service.AdminSeatService;
import com.example.ticketingproject.domain.seat.service.SeatTransactionalService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminSeatController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
public class AdminSeatControllerTest extends RestDocsSupport {

    @MockBean
    private AdminSeatService adminSeatService;

    @MockBean
    private SeatTransactionalService seatTransactionalService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private SeatResponse seatResponse;

    @Test
    @WithMockUser(roles = "ADMIN")
    void 좌석_생성_테스트() throws Exception {

        // given
        String requestBody = """
                {
                    "gradeName": "VIP",
                    "rowName": "A",
                    "seatNumber": "10"
                }
                """;

        when(adminSeatService.saveRedissonLock(eq(1L), any(CreateSeatRequest.class))).thenReturn(seatResponse);

        // when & then
        mockMvc.perform(post("/admin/venues/{venueId}/seats/redisson", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated()) // 201 Created 응답 확인
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS"))
                .andDo(restDocsHandler("admin-seat-create"));
    }
}
