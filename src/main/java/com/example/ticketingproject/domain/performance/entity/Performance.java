package com.example.ticketingproject.domain.performance.entity;

import com.example.ticketingproject.common.entity.ModifiableEntity;
import com.example.ticketingproject.domain.venue.entity.Venue;
import com.example.ticketingproject.domain.work.entity.Work;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
@Table(name = "performances")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Length(max = 20)
    private String season;

    @NotBlank
    private LocalDate startDate;

    @NotBlank
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
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
}
