package com.example.ticketingproject.domain.charge.entity;

import com.example.ticketingproject.common.entity.CreatableEntity;
import com.example.ticketingproject.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cashCharges")
public class Charge extends CreatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 10, scale = 2, nullable = false)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal amount;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(precision = 10, scale = 2, nullable = false)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal balanceAfterCharge;

    @Builder
    public Charge(User user,
                   User admin,
                   BigDecimal amount,
                   BigDecimal balanceAfterCharge) {
        this.user = user;
        this.admin = admin;
        this.amount = amount;
        this.balanceAfterCharge = balanceAfterCharge;
    }
}
