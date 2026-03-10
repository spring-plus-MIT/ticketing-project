package com.example.ticketingproject.domain.review.repository;

import com.example.ticketingproject.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByWorkIdAndDeletedAtIsNull(Long workId);

    List<Review> findAllByWorkIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long workId);
}