package com.example.ticketingproject.domain.performance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Table(name = "performances")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performance extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id")
    private Work work;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Length(max = 20)
    private String season;

    @NotBlank
    private LocalDateTime start_date;

    @NotBlank
    private LocalDateTime end_date;

    @Enumerated(EnumType.STRING)
    private PerformanceStatus status;

    @Builder
    public Performance(Work work, Venue venue, String season, LocalDateTime start_date, LocalDateTime end_date,  PerformanceStatus status) {
        this.work = work;
        this.venue = venue;
        this.season = season;
        this.start_date = start_date;
        this.end_date = end_date;
        this.status = status;
    }
}
