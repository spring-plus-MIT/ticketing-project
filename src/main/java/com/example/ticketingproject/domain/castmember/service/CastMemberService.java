package com.example.ticketingproject.domain.castmember.service;

import com.example.ticketingproject.domain.castmember.dto.CastMemberResponse;
import com.example.ticketingproject.domain.castmember.entity.CastMember;
import com.example.ticketingproject.domain.castmember.repository.CastMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CastMemberService {

    private final CastMemberRepository castMemberRepository;

    public Page<CastMemberResponse> getCastMembers(Long sessionId, Pageable pageable) {
        return castMemberRepository.findByPerformanceSessionId(sessionId, pageable)
                .map(this::convertToResponse);
    }


    private CastMemberResponse convertToResponse(CastMember c) {
        return CastMemberResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .roleName(c.getRoleName())
                .build();
    }

}