package com.example.ticketingproject.domain.performance.entity;

import com.example.ticketingproject.common.entity.ModifiableEntity;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.work.entity.Work;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
@Table(name = "performances")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @NotNull
    @Length(min = 1, max = 20)
    @Column(nullable = false, length = 20)
    private String season;

    @NotNull
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    private LocalDate endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformanceStatus status;

    @Builder
    public Performance(Work work, Venue venue, String season, LocalDate startDate, LocalDate endDate, PerformanceStatus status) {
        this.work = work;
        this.venue = venue;
        this.season = season;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status != null ? status : PerformanceStatus.UPCOMING;
    }

    public void update(Work work, Venue venue, String season, LocalDate startDate, LocalDate endDate, PerformanceStatus status) {
        this.work = work;
        this.venue = venue;
        this.season = season;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public void close() {
        this.status = PerformanceStatus.CLOSED;
    }
}