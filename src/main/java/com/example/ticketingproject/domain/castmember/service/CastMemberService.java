package com.example.ticketingproject.domain.castmember.service;

import com.example.ticketingproject.domain.castmember.dto.CastMemberRequest;
import com.example.ticketingproject.domain.castmember.dto.CastMemberResponse;
import com.example.ticketingproject.domain.castmember.entity.CastMember;
import com.example.ticketingproject.domain.castmember.repository.CastMemberRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CastMemberService {

    private final CastMemberRepository castMemberRepository;
    private final PerformanceSessionRepository performanceSessionRepository;

    @Transactional
    public void createCastMember(Long sessionId, CastMemberRequest request) {
        PerformanceSession session = performanceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회차를 찾을 수 없습니다."));

        CastMember castMember = CastMember.builder()
                .performanceSession(session)
                .name(request.getName())
                .roleName(request.getRoleName())
                .build();

        castMemberRepository.save(castMember);
    }

    @Transactional(readOnly = true)
    public List<CastMemberResponse> getCastMembers(Long sessionId) {
        return castMemberRepository.findByPerformanceSessionId(sessionId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void updateCastMember(Long castId, CastMemberRequest request) {
        CastMember castMember = castMemberRepository.findById(castId)
                .orElseThrow(() -> new IllegalArgumentException("출연진 정보를 찾을 수 없습니다."));

        // Entity에 update 메서드 추가가 필요할 수 있습니다.
    }

    @Transactional
    public void deleteCastMember(Long castId) {
        CastMember castMember = castMemberRepository.findById(castId)
                .orElseThrow(() -> new IllegalArgumentException("출연진 정보를 찾을 수 없습니다."));
        castMemberRepository.delete(castMember);
    }

    private CastMemberResponse convertToResponse(CastMember c) {
        return CastMemberResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .roleName(c.getRoleName())
                .build();
    }
}