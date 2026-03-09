package com.example.ticketingproject.domain.performancesession.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.domain.performance.entity.Performance;
import com.example.ticketingproject.domain.venue.entity.Venue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "performance_sessions")
public class PerformanceSession extends DeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    private LocalDateTime sessionDateTime ;

    @Builder
    public PerformanceSession(Long id, Performance performance, Venue venue, LocalDateTime sessionDateTime ) {
        this.id = id;
        this.performance = performance;
        this.venue = venue;
        this.sessionDateTime  = sessionDateTime ;
    }

    public void update(Venue venue, LocalDateTime sessionDateTime) {
        this.venue = venue;
        this.sessionDateTime  = sessionDateTime ;
    }
}
