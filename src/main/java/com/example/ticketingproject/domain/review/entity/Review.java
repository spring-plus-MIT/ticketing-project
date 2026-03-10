package com.example.ticketingproject.domain.review.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.work.entity.Work;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews", indexes = {
        // 특정 작품의 리뷰를 최신순으로 조회하는 쿼리 성능 최적화
        @Index(name = "idx_review_work_created", columnList = "work_id, created_at DESC")
})
public class Review extends DeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private Integer rating;

    @Builder
    public Review(User user, Work work, String content, Integer rating) {
        validateRating(rating);
        this.user = user;
        this.work = work;
        this.content = content;
        this.rating = rating;
    }

    // [무거운 엔티티 로직]
    public void update(String content, Integer rating, Long requesterId) {
        validateOwner(requesterId);
        validateRating(rating);
        this.content = content;
        this.rating = rating;
    }

    public void validateOwner(Long requesterId) {
        if (!this.user.getId().equals(requesterId)) {
            throw new AccessDeniedException("본인의 리뷰만 관리할 수 있습니다.");
        }
    }

    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("별점은 1~5점 사이여야 합니다.");
        }
    }
}