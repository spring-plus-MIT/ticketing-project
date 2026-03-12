package com.example.ticketingproject.domain.castmember.service;

import com.example.ticketingproject.domain.castmember.dto.CastMemberRequest;
import com.example.ticketingproject.domain.castmember.entity.CastMember;
import com.example.ticketingproject.domain.castmember.exception.CastMemberException;
import com.example.ticketingproject.domain.castmember.repository.CastMemberRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class AdminCastMemberServiceTest {

    @Mock
    private CastMemberRepository castMemberRepository;

    @Mock
    private PerformanceSessionRepository performanceSessionRepository;

    @InjectMocks
    private AdminCastMemberService adminCastMemberService;

    private PerformanceSession session;
    private CastMember castMember;
    private CastMemberRequest request;

    @BeforeEach
    void setUp() {
        session = PerformanceSession.builder().build();
        ReflectionTestUtils.setField(session, "id", 1L);

        castMember = CastMember.builder()
                .performanceSession(session)
                .name("홍길동")
                .roleName("주인공")
                .build();
        ReflectionTestUtils.setField(castMember, "id", 1L);

        request = new CastMemberRequest();
        ReflectionTestUtils.setField(request, "name", "김철수");
        ReflectionTestUtils.setField(request, "roleName", "악당");
    }

    @Test
    @DisplayName("캐스트 멤버 생성 성공")
    void createCastMember_success() {
        // given
        given(performanceSessionRepository.findById(1L)).willReturn(Optional.of(session));

        // when
        adminCastMemberService.createCastMember(1L, request);

        // then
        verify(performanceSessionRepository).findById(1L);
        verify(castMemberRepository).save(any(CastMember.class));
    }

    @Test
    @DisplayName("캐스트 멤버 생성 실패 - 회차(Session)를 찾을 수 없음")
    void createCastMember_fail_sessionNotFound() {
        // given
        given(performanceSessionRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCastMemberService.createCastMember(1L, request))
                .isInstanceOf(PerformanceSessionException.class);
    }

    @Test
    @DisplayName("캐스트 멤버 수정 성공")
    void updateCastMember_success() {
        // given
        given(castMemberRepository.findById(1L)).willReturn(Optional.of(castMember));

        // when
        adminCastMemberService.updateCastMember(1L, request);

        // then
        assertThat(castMember.getName()).isEqualTo("김철수");
        assertThat(castMember.getRoleName()).isEqualTo("악당");
        verify(castMemberRepository).findById(1L);
    }

    @Test
    @DisplayName("캐스트 멤버 수정 실패 - 출연진을 찾을 수 없음")
    void updateCastMember_fail_castNotFound() {
        // given
        given(castMemberRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adminCastMemberService.updateCastMember(999L, request))
                .isInstanceOf(CastMemberException.class);
    }

    @Test
    @DisplayName("캐스트 멤버 삭제 성공")
    void deleteCastMember_success() {
        // given
        given(castMemberRepository.findById(1L)).willReturn(Optional.of(castMember));

        // when
        adminCastMemberService.deleteCastMember(1L);

        // then
        verify(castMemberRepository).findById(1L);
        verify(castMemberRepository).delete(castMember);
    }
}