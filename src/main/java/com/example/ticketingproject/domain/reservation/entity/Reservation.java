package com.example.ticketingproject.domain.reservation.entity;

import com.example.ticketingproject.common.entity.ModifiableEntity;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.seat.entity.Seat;
import com.example.ticketingproject.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
public class Reservation extends ModifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 공연 회차
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performance_session_id", nullable = false)
    private PerformanceSession performanceSession;

    // 좌석
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
    public Reservation(
            User user,
            PerformanceSession performanceSession,
            Seat seat,
            ReservationStatus status,
            BigDecimal totalPrice,
            LocalDateTime reservedAt,
            LocalDateTime expiresAt
    ) {
        this.user = user;
        this.performanceSession = performanceSession;
        this.seat = seat;
        this.status = status;
        this.totalPrice = totalPrice;
        this.reservedAt = reservedAt;
        this.expiresAt = expiresAt;
    }

    // 예약 취소
    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    // 결제 완료
    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }
}
