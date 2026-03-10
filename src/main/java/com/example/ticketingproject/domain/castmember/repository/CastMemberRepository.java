package com.example.ticketingproject.domain.castmember.repository;

import com.example.ticketingproject.domain.castmember.entity.CastMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CastMemberRepository extends JpaRepository<CastMember, Long> {
    List<CastMember> findByPerformanceSessionId(Long sessionId);
}