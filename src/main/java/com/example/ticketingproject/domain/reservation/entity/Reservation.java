package com.example.ticketingproject.domain.reservation.entity;

import com.example.ticketingproject.common.entity.ModifiableEntity;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.performancesession.entity.PerformanceSession;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
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

    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }

    public void cancel() {
        switch (this.status) {
            case CANCELED -> throw new ReservationException(ErrorStatus.ALREADY_CANCELED_RESERVATION);
            case CONFIRMED -> throw new ReservationException(ErrorStatus.ALREADY_PAID_RESERVATION);
        }
        this.status = ReservationStatus.CANCELED;
    }

    public void confirm() {
        switch (this.status) {
            case CANCELED -> throw new ReservationException(ErrorStatus.ALREADY_CANCELED_RESERVATION);
            case CONFIRMED -> throw new ReservationException(ErrorStatus.ALREADY_PAID_RESERVATION);
        }

        this.status = ReservationStatus.CONFIRMED;
    }

    public void validateNotPaid(boolean alreadyPaid) {
        if (alreadyPaid) {
            throw new ReservationException(ErrorStatus.ALREADY_PAID_RESERVATION);
        }
    }
}
