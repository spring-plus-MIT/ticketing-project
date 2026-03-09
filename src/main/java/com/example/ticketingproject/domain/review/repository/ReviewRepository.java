package com.example.ticketingproject.domain.review.repository;

import com.example.ticketingproject.domain.review.entity.Review;
import com.example.ticketingproject.domain.work.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllByWorkAndDeletedAtIsNull(Work work, Pageable pageable);
}