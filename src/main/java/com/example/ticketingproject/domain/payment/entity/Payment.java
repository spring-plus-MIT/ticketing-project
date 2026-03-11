package com.example.ticketingproject.domain.payment.entity;

import com.example.ticketingproject.common.entity.CreatableEntity;
import com.example.ticketingproject.domain.payment.enums.PaymentStatus;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment extends CreatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JoinColumn(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private BigDecimal balanceAfterCharge;

    @Builder
    public Payment(Reservation reservation, User user, BigDecimal amount, PaymentStatus paymentStatus,BigDecimal balanceAfterCharge) {
        this.reservation = reservation;
        this.user = user;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.balanceAfterCharge = balanceAfterCharge;
    }
}
