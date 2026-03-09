package com.example.ticketingproject.domain.cashCharge.entity;

import com.example.ticketingproject.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class CashCharge extends CreatableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private BigDecimal balanceAfterCharge;

    @Builder
    private CashCharge (User user,
                        User admin,
                        BigDecimal amount,
                        BigDecimal balanceAfterCharge) {
        this.user = user;
        this.admin = admin;
        this.amount = amount;
        this.balanceAfterCharge = balanceAfterCharge;
    }

    @Builder
    public static CashCharge create(User user,
                                     User admin,
                                     BigDecimal amount,
                                     BigDecimal balanceAfterCharge) {
        return new CashCharge(user, admin, amount, balanceAfterCharge);
    }
}
