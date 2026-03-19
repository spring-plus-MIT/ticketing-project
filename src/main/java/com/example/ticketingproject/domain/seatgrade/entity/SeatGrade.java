package com.example.ticketingproject.domain.seatgrade.entity;

import com.example.ticketingproject.common.entity.DeletableEntity;
import com.example.ticketingproject.common.enums.GradeName;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.seatgrade.exeption.SeatGradeException;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.enums.ErrorStatus.SEAT_GRADE_CAPACITY_EXCEEDED;
import static com.example.ticketingproject.common.util.Constants.*;

@Getter
@Entity
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat_grades")
public class SeatGrade extends DeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_session_id", nullable = false)
    private PerformanceSession performanceSession;

    @NotNull
    @Enumerated(EnumType.STRING)
    private GradeName gradeName;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @NotNull
    @Min(value = 0)
    private int totalSeats;

    @NotNull
    @Min(value = 0)
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

    public void decreaseRemainingSeats() {
        if (this.remainingSeats <= 0) {
            throw new SeatGradeException(SEAT_GRADE_CAPACITY_EXCEEDED.getHttpStatus(), SEAT_GRADE_CAPACITY_EXCEEDED);
        }
        this.remainingSeats--;
    }
}