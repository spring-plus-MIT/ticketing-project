package com.example.ticketingproject.domain.seatgrade.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.domain.performance_session.entity.PerformanceSession;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder; // 추가
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat_grades")
public class SeatGrade extends DeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_session_id", nullable = false)
    private PerformanceSession performanceSession;

    @Column(nullable = false)
    private String gradeName;

    @Column(nullable = false)
    private Integer price;

    @Builder
    public SeatGrade(PerformanceSession performanceSession, String gradeName, Integer price) {
        this.performanceSession = performanceSession;
        this.gradeName = gradeName;
        this.price = price;
    }
}