package com.example.ticketingproject.domain.review.repository;

import com.example.ticketingproject.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByWorkId(Long workId, Pageable pageable);

    Optional<Review> findById(Long reviewId);
}
