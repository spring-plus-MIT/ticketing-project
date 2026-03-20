package com.example.ticketingproject.domain.like.controller;

import com.example.ticketingproject.RestDocsSupport;
import com.example.ticketingproject.domain.like.dto.LikeResponse;
import com.example.ticketingproject.domain.like.service.LikeService;
import com.example.ticketingproject.domain.user.enums.UserRole;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@MockBean(JpaMetamodelMappingContext.class)
@Import(SecurityConfig.class)
class LikeControllerTest extends RestDocsSupport {

    @MockBean
    private LikeService likeService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private CustomUserDetails mockUser() {
        return new CustomUserDetails(1L, "user@test.com", UserRole.USER);
    }

    private LikeResponse createSampleResponse() {
        return LikeResponse.builder()
                .likeId(1L)
                .workId(1L)
                .userId(1L)
                .build();
    }

    @Test
    @DisplayName("좋아요 등록 성공")
    void create_like_success() throws Exception {
        // given
        given(likeService.save(eq(1L), eq(1L)))
                .willReturn(createSampleResponse());

        // when & then
        mockMvc.perform(post("/works/{workId}/likes", 1L)
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201_CREATE_SUCCESS"))
                .andExpect(jsonPath("$.data.likeId").value(1L))
                .andExpect(jsonPath("$.data.workId").value(1L))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andDo(restDocsHandler("like-create"));
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void delete_like_success() throws Exception {
        // given
        given(likeService.delete(eq(1L), eq(1L), eq(1L)))
                .willReturn(createSampleResponse());

        // when & then
        mockMvc.perform(delete("/works/{workId}/likes/{likeId}", 1L, 1L)
                        .with(user(mockUser()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200_DELETE_SUCCESS"))
                .andExpect(jsonPath("$.data.likeId").value(1L))
                .andExpect(jsonPath("$.data.workId").value(1L))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andDo(restDocsHandler("like-delete"));
    }
}