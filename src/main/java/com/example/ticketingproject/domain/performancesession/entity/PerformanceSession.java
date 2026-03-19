package com.example.ticketingproject.domain.performancesession.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.venue.entity.Venue;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.ticketingproject.common.util.Constants.MSG_VALIDATION_NOT_NULL_ERROR;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "performance_sessions")
public class PerformanceSession extends DeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime endTime;

    @Builder
    public PerformanceSession(Performance performance, Venue venue, LocalDateTime startTime, LocalDateTime endTime) {
        this.performance = performance;
        this.venue = venue;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void update(Venue venue, LocalDateTime startTime, LocalDateTime endTime) {
        this.venue = venue;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}