package com.example.ticketingproject.domain.review.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.work.entity.Work;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
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


    public Review(User user, Work work, String content, Integer rating) {
        this.user = user;
        this.work = work;
        this.content = content;
        this.rating = rating;
    }
}