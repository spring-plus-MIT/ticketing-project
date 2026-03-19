package com.example.ticketingproject.domain.review.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.work.entity.Work;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.validator.constraints.Length;

@Getter
@Entity
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
public class Review extends DeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @NotBlank
    @Length(max = 200)
    private String content;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

    @Builder
    public Review(User user, Work work, String content, Integer rating) {
        this.user = user;
        this.work = work;
        this.content = content;
        this.rating = rating;
    }
    public void update(String content, Integer rating) {
        this.content = content;
        this.rating = rating;
    }
}