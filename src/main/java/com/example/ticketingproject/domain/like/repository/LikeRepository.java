package com.example.ticketingproject.domain.like.repository;

import com.example.ticketingproject.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Boolean existsByUserIdAndWorkId(Long userId, Long workId);
}
