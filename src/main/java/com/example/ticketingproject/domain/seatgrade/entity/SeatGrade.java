package com.example.ticketingproject.domain.seatgrade.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.domain.performance_session.entity.PerformanceSession;
import com.example.ticketingproject.domain.seatgrade.enums.GradeName;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    // String -> GradeName으로 변경하고 @Enumerated 추가
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeName gradeName;

    @Column(nullable = false)
    private Integer price;

    @Builder
    public SeatGrade(PerformanceSession performanceSession, GradeName gradeName, Integer price) {
        this.performanceSession = performanceSession;
        this.gradeName = gradeName;
        this.price = price;
    }
}