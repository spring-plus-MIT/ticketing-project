package com.example.ticketingproject.domain.castmember.service;

import com.example.ticketingproject.domain.castmember.dto.CastMemberRequest;
import com.example.ticketingproject.domain.castmember.entity.CastMember;
import com.example.ticketingproject.domain.castmember.exception.CastMemberException;
import com.example.ticketingproject.domain.castmember.repository.CastMemberRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.CAST_MEMBER_NOT_FOUND;
import static com.example.ticketingproject.common.enums.ErrorStatus.SESSION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCastMemberService {
    private final CastMemberRepository castMemberRepository;
    private final PerformanceSessionRepository performanceSessionRepository;

    @Transactional
    public void createCastMember(Long sessionId, CastMemberRequest request) {
        PerformanceSession session = performanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new PerformanceSessionException(SESSION_NOT_FOUND.getHttpStatus(), SESSION_NOT_FOUND));

        CastMember castMember = CastMember.builder()
                .performanceSession(session)
                .name(request.getName())
                .roleName(request.getRoleName())
                .build();

        castMemberRepository.save(castMember);
    }

    @Transactional
    public void updateCastMember(Long castId, CastMemberRequest request) {
        CastMember castMember = castMemberRepository.findById(castId)
                .orElseThrow(() -> new CastMemberException(CAST_MEMBER_NOT_FOUND.getHttpStatus(), CAST_MEMBER_NOT_FOUND));

        castMember.update(request.getName(), request.getRoleName());
    }

    @Transactional
    public void deleteCastMember(Long castId) {
        CastMember castMember = castMemberRepository.findById(castId)
                .orElseThrow(() -> new CastMemberException(CAST_MEMBER_NOT_FOUND.getHttpStatus(), CAST_MEMBER_NOT_FOUND));

        castMemberRepository.delete(castMember);
    }

}
