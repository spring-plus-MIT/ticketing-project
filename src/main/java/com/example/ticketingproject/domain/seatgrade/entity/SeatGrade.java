package com.example.ticketingproject.domain.seatgrade.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.common.enums.GradeName; // common에 있는 Enum을 가져옵니다.
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeName gradeName; // 이제 common의 GradeName을 사용합니다.

    @Column(nullable = false)
    private BigDecimal price;

    @NotBlank
    private int totalSeats;

    @NotBlank
    private int remainingSeats;



    @Builder
    public SeatGrade(PerformanceSession performanceSession, GradeName gradeName, BigDecimal price, int totalSeats, int remainingSeats) {
        this.performanceSession = performanceSession;
        this.gradeName = gradeName;
        this.price = price;
        this.totalSeats = totalSeats;
        this.remainingSeats = remainingSeats;
    }

    public void update(GradeName gradeName) {
        this.gradeName = gradeName;
    }
}