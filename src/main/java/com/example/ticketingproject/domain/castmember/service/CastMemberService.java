package com.example.ticketingproject.domain.castmember.service;

import com.example.ticketingproject.domain.castmember.dto.CastMemberResponse;
import com.example.ticketingproject.domain.castmember.entity.CastMember;
import com.example.ticketingproject.domain.castmember.repository.CastMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CastMemberService {

    private final CastMemberRepository castMemberRepository;

    public List<CastMemberResponse> getCastMembers(Long sessionId) {
        return castMemberRepository.findByPerformanceSessionId(sessionId).stream()
                .map(this::convertToResponse)
                .toList();
    }


    private CastMemberResponse convertToResponse(CastMember c) {
        return CastMemberResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .roleName(c.getRoleName())
                .build();
    }

}