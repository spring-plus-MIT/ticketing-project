package com.example.ticketingproject.domain.review.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.review.dto.request.ReviewRequestDto;
import com.example.ticketingproject.domain.review.dto.response.ReviewResponseDto;
import com.example.ticketingproject.domain.review.service.ReviewService;
import com.example.ticketingproject.security.CustomUserDetails;
import com.example.ticketingproject.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(ReviewController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ReviewControllerTest extends RestDocsSupport {

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("유저 - 리뷰 목록 조회 성공")
    @WithMockUser
    void get_reviews_success() throws Exception {
        // given
        Long workId = 1L;
        given(reviewService.findAll(eq(workId), any(Pageable.class))).willReturn(null);

        // when & then
        mockMvc.perform(get("/works/{workId}/reviews", workId)
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocsHandler("review-get-list"));
    }

    @Test
    @DisplayName("유저 - 리뷰 작성 성공")
    void create_review_success() throws Exception {
        CustomUserDetails mockUserDetails = Mockito.mock(CustomUserDetails.class);

        when(mockUserDetails.getId()).thenReturn(1L);
        when(mockUserDetails.getAuthorities()).thenReturn(Collections.emptyList());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                mockUserDetails,
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long workId = 1L;
        ReviewRequestDto request = new ReviewRequestDto();
        ReflectionTestUtils.setField(request, "content", "정말 감동적인 공연이었어요!");
        ReflectionTestUtils.setField(request, "rating", 5);

        ReviewResponseDto response = Mockito.mock(ReviewResponseDto.class);
        when(response.getId()).thenReturn(1L);
        when(response.getContent()).thenReturn("정말 감동적인 공연이었어요!");
        when(response.getRating()).thenReturn(5);

        given(reviewService.createReview(eq(workId), any(ReviewRequestDto.class), any(Long.class)))
                .willReturn(response);

        mockMvc.perform(post("/works/{workId}/reviews", workId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(restDocsHandler("review-create"));
    }
}