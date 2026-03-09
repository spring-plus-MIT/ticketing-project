package com.example.ticketingproject.domain.seatgrade.entity;

import com.example.ticketingproject.common.entity.DeletableEntity; // 팀 공통 부모
import com.example.ticketingproject.domain.performance.entity.PerformanceSession;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat_grades")
public class SeatGrade extends DeletableEntity { // 1. 상속 이름 변경

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

    // 2. deletedAt 필드는 부모에게 있으므로 여기서 삭제했습니다!

    public SeatGrade(PerformanceSession performanceSession, String gradeName, Integer price) {
        this.performanceSession = performanceSession;
        this.gradeName = gradeName;
        this.price = price;
    }
}