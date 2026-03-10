package com.example.ticketingproject.domain.reservation.entity;

import com.example.ticketingproject.common.entity.DeletableEntity; // 상속 변경 확인
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLRestriction; // 추가

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@SQLRestriction("deleted_at IS NULL") // 튜터님 피드백 반영
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
public class Reservation extends DeletableEntity { // Soft Delete 필드 사용을 위해 변경

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performance_session_id", nullable = false)
    private PerformanceSession performanceSession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @NotNull
    private BigDecimal totalPrice;

    private LocalDateTime reservedAt;

    private LocalDateTime expiresAt;

    @Builder
    public Reservation(User user, PerformanceSession performanceSession, Seat seat,
                       ReservationStatus status, BigDecimal totalPrice,
                       LocalDateTime reservedAt, LocalDateTime expiresAt) {
        this.user = user;
        this.performanceSession = performanceSession;
        this.seat = seat;
        this.status = status;
        this.totalPrice = totalPrice;
        this.reservedAt = reservedAt;
        this.expiresAt = expiresAt;
    }
}