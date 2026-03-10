package com.example.ticketingproject.domain.like.service;

import com.example.ticketingproject.domain.like.dto.LikeResponse;
import com.example.ticketingproject.domain.like.entity.Like;
import com.example.ticketingproject.domain.like.exception.LikeException;
import com.example.ticketingproject.domain.like.repository.LikeRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.enums.UserRole;
import com.example.ticketingproject.domain.user.enums.UserStatus;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import com.example.ticketingproject.domain.work.entity.Work;
import com.example.ticketingproject.domain.work.enums.Category;
import com.example.ticketingproject.domain.work.exception.WorkException;
import com.example.ticketingproject.domain.work.repository.WorkRepository;
import com.example.ticketingproject.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkRepository workRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    private User user;
    private Work work;
    private Like like;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() throws Exception {
        user = User.builder()
                .name("홍길동")
                .email("test@123.com")
                .password("1234")
                .phone("010-1234-1234")
                .balance(new BigDecimal(1000))
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        Constructor<Work> constructor = Work.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        work = constructor.newInstance();

        ReflectionTestUtils.setField(work, "id", 1L);
        ReflectionTestUtils.setField(work, "title", "오케스트라 연주회");
        ReflectionTestUtils.setField(work, "category", Category.CLASSIC);
        ReflectionTestUtils.setField(work, "description", "7일간 진행");
        ReflectionTestUtils.setField(work, "minPrice", BigDecimal.valueOf(700));
        ReflectionTestUtils.setField(work, "likeCount", 50L);

        like = Like.builder()
                .work(work)
                .user(user)
                .build();
        ReflectionTestUtils.setField(like, "id", 1L);
    }

    @Test
    @DisplayName("찜 저장 성공")
    void save_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(likeRepository.save(any(Like.class))).willReturn(like);

        LikeResponse response = likeService.save(1L, 1L);

        assertThat(response.getLikeId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getWorkId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("찜 저장 실패 - 유저 없음")
    void save_userNotFound() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.save(1L, 1L))
                .isInstanceOf(UserException.class);

        then(workRepository).should(never()).findById(any());
        then(likeRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("찜 저장 실패 - 작품 없음")
    void save_workNotFound() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(workRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.save(1L, 1L))
                .isInstanceOf(WorkException.class);

        then(likeRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("찜 삭제 성공")
    void delete_success() {
        customUserDetails = mock(CustomUserDetails.class);
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(likeRepository.findById(1L)).willReturn(Optional.of(like));
        given(customUserDetails.getId()).willReturn(1L);

        LikeResponse response = likeService.delete(1L, 1L, customUserDetails);

        assertThat(response.getLikeId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getWorkId()).isEqualTo(1L);
        then(likeRepository).should(times(1)).delete(like);
    }

    @Test
    @DisplayName("찜 삭제 실패 - 작품 없음")
    void delete_workNotFound() {
        customUserDetails = mock(CustomUserDetails.class);
        given(workRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.delete(1L, 1L, customUserDetails))
                .isInstanceOf(WorkException.class);

        then(likeRepository).should(never()).findById(any());
        then(likeRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("찜 삭제 실패 - 찜 없음")
    void delete_likeNotFound() {
        customUserDetails = mock(CustomUserDetails.class);
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(likeRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.delete(1L, 1L, customUserDetails))
                .isInstanceOf(LikeException.class);

        then(likeRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("찜 삭제 실패 - 권한 없음")
    void delete_forbidden() {
        customUserDetails = mock(CustomUserDetails.class);
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(likeRepository.findById(1L)).willReturn(Optional.of(like));
        given(customUserDetails.getId()).willReturn(999L); // 다른 유저 ID

        assertThatThrownBy(() -> likeService.delete(1L, 1L, customUserDetails))
                .isInstanceOf(LikeException.class);

        then(likeRepository).should(never()).delete(any());
    }
}