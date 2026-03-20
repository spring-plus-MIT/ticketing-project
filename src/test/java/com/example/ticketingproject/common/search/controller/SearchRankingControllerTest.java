package com.example.ticketingproject.common.search.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.common.search.service.SearchRankingService;
import com.example.ticketingproject.security.SecurityConfig;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchRankingController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class SearchRankingControllerTest extends RestDocsSupport {

    @MockBean
    private SearchRankingService searchRankingService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("인기 검색어 조회")
    @WithAnonymousUser
    void get_popular_keywords_with_domain_success() throws Exception {
        // given
        List<String> keywords = List.of("아이유콘서트", "BTS", "뉴진스");

        given(searchRankingService.getRealTimeKeywords(eq("concert")))
                .willReturn(keywords);

        // when & then
        mockMvc.perform(get("/search/popular")
                        .param("domain", "concert")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data[0]").value("아이유콘서트"))
                .andExpect(jsonPath("$.data[1]").value("BTS"))
                .andExpect(jsonPath("$.data").isArray())
                .andDo(restDocsHandler("search-popular"));
    }
}