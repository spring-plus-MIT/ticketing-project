package com.example.ticketingproject.domain.like.service;

import com.example.ticketingproject.auth.exception.AuthException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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


        work = Work.builder()
                .title("오케스트라 연주회")
                .category(Category.CLASSIC)
                .description("7일간 진행")
                .likeCount(50L)
                .build();
        ReflectionTestUtils.setField(work, "id", 1L);


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
        given(likeRepository.existsByUserIdAndWorkId(1L, 1L)).willReturn(false);
        given(likeRepository.save(any(Like.class))).willReturn(like);

        LikeResponse response = likeService.save(1L, 1L);

        assertThat(response.getLikeId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getWorkId()).isEqualTo(1L);

        then(workRepository).should(times(1)).incrementLikeCount(1L);
    }

    @Test
    @DisplayName("찜 저장 실패 - 작품 없음")
    void save_workNotFound() {
        given(workRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.save(1L, 1L))
                .isInstanceOf(WorkException.class);

        then(userRepository).should(never()).findById(any());
        then(likeRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("찜 저장 실패 - 유저 없음")
    void save_userNotFound() {
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.save(1L, 1L))
                .isInstanceOf(UserException.class);

        then(likeRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("찜 저장 실패 - 이미 찜한 작품")
    void save_likeAlreadyExists() {
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(likeRepository.existsByUserIdAndWorkId(1L, 1L)).willReturn(true);

        assertThatThrownBy(() -> likeService.save(1L, 1L))
                .isInstanceOf(LikeException.class);

        then(likeRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("찜 삭제 성공")
    void delete_success() {
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(likeRepository.findById(1L)).willReturn(Optional.of(like));

        LikeResponse response = likeService.delete(1L, 1L, 1L);

        assertThat(response.getLikeId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getWorkId()).isEqualTo(1L);
        then(likeRepository).should(times(1)).delete(like);
        then(workRepository).should(times(1)).decreaseLikeCount(1L);
    }

    @Test
    @DisplayName("찜 삭제 실패 - 작품 없음")
    void delete_workNotFound() {
        given(workRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.delete(1L, 1L, 1L))
                .isInstanceOf(WorkException.class);

        then(likeRepository).should(never()).findById(any());
        then(likeRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("찜 삭제 실패 - 찜 없음")
    void delete_likeNotFound() {
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(likeRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> likeService.delete(1L, 1L, 1L))
                .isInstanceOf(LikeException.class);

        then(likeRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("찜 삭제 실패 - 작품과 찜 불일치")
    void delete_likeMismatch() {
        Work otherWork = mock(Work.class);
        given(otherWork.getId()).willReturn(999L);

        given(workRepository.findById(999L)).willReturn(Optional.of(otherWork));
        given(likeRepository.findById(1L)).willReturn(Optional.of(like));

        assertThatThrownBy(() -> likeService.delete(999L, 1L, 1L))
                .isInstanceOf(LikeException.class);

        then(likeRepository).should(never()).delete(any());
    }

    @Test
    @DisplayName("찜 삭제 실패 - 권한 없음")
    void delete_forbidden() {
        given(workRepository.findById(1L)).willReturn(Optional.of(work));
        given(likeRepository.findById(1L)).willReturn(Optional.of(like));

        assertThatThrownBy(() -> likeService.delete(1L, 1L, 999L))
                .isInstanceOf(AuthException.class);

        then(likeRepository).should(never()).delete(any());
    }
}