package com.example.ticketingproject.domain.castmember.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.castmember.dto.CastMemberResponse;
import com.example.ticketingproject.domain.castmember.service.CastMemberService;
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
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CastMemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class CastMemberControllerTest extends RestDocsSupport {

    @MockBean
    private CastMemberService castMemberService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("사용자 - 출연진 목록 페이징 조회 성공")
    @WithMockUser
    void get_cast_pages_success() throws Exception {
        // given
        CastMemberResponse response = CastMemberResponse.builder()
                .id(1L)
                .name("홍길동")
                .roleName("주연")
                .build();

        PageImpl<CastMemberResponse> pageResponse = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 10),
                1
        );

        given(castMemberService.getCastMembers(eq(1L), any())).willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/performances/{performanceId}/sessions/{sessionId}/casts", 1L, 1L)
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].name").value("홍길동"))
                .andExpect(jsonPath("$.data.content[0].roleName").value("주연"))
                .andDo(restDocsHandler("cast-get-pages"));
    }
}