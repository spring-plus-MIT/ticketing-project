package com.example.ticketingproject.domain.castmember.repository;

import com.example.ticketingproject.domain.castmember.entity.CastMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CastMemberRepository extends JpaRepository<CastMember, Long> {

    Page<CastMember> findByPerformanceSessionId(Long sessionId, Pageable pageable);
}