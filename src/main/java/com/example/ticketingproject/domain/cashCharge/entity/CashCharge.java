package com.example.ticketingproject.domain.cashCharge.entity;

import com.example.ticketingproject.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
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

    @NotBlank
    private BigDecimal amount;

    @NotBlank
    private BigDecimal balanceAfterCharge;

    protected CashCharge() {}

    private CashCharge (User user,
                        User admin,
                        BigDecimal amount,
                        BigDecimal balanceAfterCharge) {
        this.user = user;
        this.admin = admin;
        this.amount = amount;
        this.balanceAfterCharge = balanceAfterCharge;
    }

    public static CashCharge create(User user,
                                     User admin,
                                     BigDecimal amount,
                                     BigDecimal balanceAfterCharge) {
        return new CashCharge(user, admin, amount, balanceAfterCharge);
    }
}
