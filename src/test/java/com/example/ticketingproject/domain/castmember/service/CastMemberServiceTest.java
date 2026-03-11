package com.example.ticketingproject.domain.castmember.service;

import com.example.ticketingproject.domain.castmember.dto.CastMemberResponse;
import com.example.ticketingproject.domain.castmember.entity.CastMember;
import com.example.ticketingproject.domain.castmember.repository.CastMemberRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CastMemberServiceTest {

    @Mock
    private CastMemberRepository castMemberRepository;

    @InjectMocks
    private CastMemberService castMemberService;

    private CastMember castMember;

    @BeforeEach
    void setUp() {
        PerformanceSession session = PerformanceSession.builder().build();
        ReflectionTestUtils.setField(session, "id", 1L);

        castMember = CastMember.builder()
                .performanceSession(session)
                .name("홍길동")
                .roleName("주인공")
                .build();
        ReflectionTestUtils.setField(castMember, "id", 1L);
    }

    @Test
    @DisplayName("특정 회차의 캐스트 멤버 목록 조회 성공")
    void getCastMembers_success() {
        // given
        given(castMemberRepository.findByPerformanceSessionId(1L)).willReturn(List.of(castMember));

        // when
        List<CastMemberResponse> responses = castMemberService.getCastMembers(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(0).getName()).isEqualTo("홍길동");
        assertThat(responses.get(0).getRoleName()).isEqualTo("주인공");
    }
}