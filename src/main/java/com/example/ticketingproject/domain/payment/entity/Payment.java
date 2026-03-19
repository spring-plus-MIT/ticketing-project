package com.example.ticketingproject.domain.payment.entity;

import com.example.ticketingproject.common.entity.CreatableEntity;
import com.example.ticketingproject.domain.payment.enums.PaymentStatus;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

import static com.example.ticketingproject.common.util.Constants.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment extends CreatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @DecimalMin(value = "0.0", message = MSG_VALIDATION_DECIMAL_MIN_ERROR)
    @Digits(integer = 8, fraction = 0, message = MSG_VALIDATION_DIGITS_ERROR)
    @Column(nullable = false, precision = 8, scale = 0)
    private BigDecimal amount;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @NotNull(message = MSG_VALIDATION_NOT_NULL_ERROR)
    @DecimalMin(value = "0.0", message = MSG_VALIDATION_DECIMAL_MIN_ERROR)
    @Column(nullable = false)
    private BigDecimal balanceAfterPayment;

    @Builder
    public Payment(Reservation reservation, User user, BigDecimal amount, PaymentStatus paymentStatus, BigDecimal balanceAfterPayment) {
        this.reservation = reservation;
        this.user = user;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.balanceAfterPayment = balanceAfterPayment;
    }
}