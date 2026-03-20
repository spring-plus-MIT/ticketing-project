package com.example.ticketingproject.domain.seatgrade.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.seatgrade.dto.CreateSeatGradeRequest;
import com.example.ticketingproject.domain.seatgrade.dto.PutSeatGradeRequest;
import com.example.ticketingproject.domain.seatgrade.dto.SeatGradeResponse;
import com.example.ticketingproject.domain.seatgrade.service.AdminSeatGradeService;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminSeatGradeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
public class AdminSeatGradeControllerTest extends RestDocsSupport {

    @MockBean
    private AdminSeatGradeService adminSeatGradeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private SeatGradeResponse seatGradeResponse;

    @Test
    @WithMockUser(roles = "ADMIN")
    void 좌석_등급_생성_테스트() throws Exception {

        // given
        String requestBody = """
                {
                    "gradeName": "VIP",
                    "price": "200",
                    "totalSeats": "100",
                    "remainingSeats": "100"
                }
                """;

        when(adminSeatGradeService.save(eq(1L), any(CreateSeatGradeRequest.class))).thenReturn(seatGradeResponse);

        // when & then
        mockMvc.perform(post("/admin/sessions/{sessionId}/seat-grades", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated()) // 201 Created 응답 확인
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS"))
                .andDo(restDocsHandler("admin-seatgrade-create"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 좌석_등급_수정_테스트() throws Exception {

        // given
        String requestBody = """
                {
                    "gradeName": "R"
                }
                """;

        when(adminSeatGradeService.update(eq(2L), eq(2L), any(PutSeatGradeRequest.class))).thenReturn(seatGradeResponse);

        // when & then
        mockMvc.perform(put("/admin/sessions/{sessionId}/seat-grades/{seatGradeId}", 2L, 2L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_UPDATE_SUCCESS"))
                .andDo(restDocsHandler("admin-seatgrade-update"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 좌석_등급_삭제_테스트() throws Exception {

        // given
        willDoNothing().given(adminSeatGradeService).delete(3L, 3L);

        // when & then
        mockMvc.perform(delete("/admin/sessions/{sessionId}/seat-grades/{seatGradeId}", 1L, 100L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("admin-seatgrade-delete"));
    }
}
