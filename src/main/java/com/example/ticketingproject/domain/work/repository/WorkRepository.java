package com.example.ticketingproject.domain.work.repository;

import com.example.ticketingproject.domain.work.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkRepository extends JpaRepository<Work, Long> {

    @Modifying
    @Query("UPDATE Work w SET w.likeCount = w.likeCount + 1 WHERE w.id = :workId")
    void incrementLikeCount(@Param("workId") Long workId);

    @Modifying
    @Query("UPDATE Work w SET w.likeCount = w.likeCount - 1 WHERE w.id = :workId")
    void decreaseLikeCount(@Param("workId") Long workId);
}
