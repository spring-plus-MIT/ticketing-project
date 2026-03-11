package com.example.ticketingproject.domain.castmember.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.castmember.dto.CastMemberRequest;
import com.example.ticketingproject.domain.castmember.dto.CastMemberResponse;
import com.example.ticketingproject.domain.castmember.entity.CastMember;
import com.example.ticketingproject.domain.castmember.exception.CastMemberException;
import com.example.ticketingproject.domain.castmember.repository.CastMemberRepository;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.performancesession.exception.PerformanceSessionException;
import com.example.ticketingproject.domain.performancesession.repository.PerformanceSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

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