package com.example.ticketingproject.domain.seatgrade.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.seatgrade.dto.SeatGradeResponse;
import com.example.ticketingproject.domain.seatgrade.service.SeatGradeService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeatGradeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
public class SeatGradeControllerTest extends RestDocsSupport {

    @MockBean
    private SeatGradeService seatGradeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser
    void 좌석_등급_목록_조회_테스트() throws Exception {

        // given
        SeatGradeResponse response = SeatGradeResponse.builder()
                .seatGradeId(1L)
                .sessionId(1L)
                .gradeName(GradeName.VIP)
                .price(BigDecimal.valueOf(500))
                .totalSeats(300)
                .remainingSeats(300)
                .createdAt(LocalDateTime.now())
                .modifiedAt(null)
                .deletedAt(null)
                .build();

        PageImpl<SeatGradeResponse> pageResponse = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 10),
                1
        );

        given(seatGradeService.findAll(eq(1L), any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/sessions/{sessionId}/seat-grades", 1L)
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].seatGradeId").value(1L))
                .andExpect(jsonPath("$.data.content[0].price").value(500))
                .andExpect(jsonPath("$.data.content[0].createdAt").exists())
                .andDo(restDocsHandler("seatgrade-get-pages"));
    }

    @Test
    @WithMockUser
    void 좌석_등급_단건_조회_테스트() throws Exception {

        // given
        SeatGradeResponse response = SeatGradeResponse.builder()
                .seatGradeId(2L)
                .sessionId(2L)
                .gradeName(GradeName.R)
                .price(BigDecimal.valueOf(300))
                .totalSeats(100)
                .remainingSeats(100)
                .createdAt(LocalDateTime.now())
                .modifiedAt(null)
                .deletedAt(null)
                .build();

        given(seatGradeService.findOne(eq(2L), eq(2L))).willReturn(response);

        // when & then
        mockMvc.perform(get("/sessions/{sessionId}/seat-grades/{seatGradeId}", 2L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.seatGradeId").value(2L))
                .andExpect(jsonPath("$.data.price").value(300))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andDo(restDocsHandler("seatgrade-get-detail"));
    }
}
