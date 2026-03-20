package com.example.ticketingproject.domain.review.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.service.AdminReviewService;
import com.example.ticketingproject.domain.review.service.ReviewService;
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

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminReviewController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class AdminReviewControllerTest extends RestDocsSupport {

    @MockBean
    private AdminReviewService adminReviewService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("관리자 - 리뷰 목록 조회 성공")
    @WithMockUser(roles = "ADMIN")
    void get_all_reviews_success() throws Exception {
        Long workId = 1L;
        ReviewResponseDto response = ReviewResponseDto.builder()
                .id(1L)
                .content("좋은 작품이에요")
                .build();

        Page<ReviewResponseDto> pageResponse = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);

        given(reviewService.findAll(workId, PageRequest.of(0,10))).willReturn(pageResponse);

        mockMvc.perform(get("/admin/works/{workId}/reviews", workId)
                        .param("page","1")
                        .param("size","10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_READ_SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].content").value("좋은 작품이에요"))
                .andDo(restDocsHandler("admin-review-get-list"));
    }

    @Test
    @DisplayName("관리자 - 리뷰 삭제 성공")
    @WithMockUser(roles = "ADMIN")
    void delete_review_success() throws Exception {
        Long workId = 1L;
        Long reviewId = 1L;

        willDoNothing().given(adminReviewService).deleteReviewByAdmin(reviewId, workId);

        mockMvc.perform(delete("/admin/works/{workId}/reviews/{reviewId}", workId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andDo(restDocsHandler("admin-review-delete"));
    }
}
