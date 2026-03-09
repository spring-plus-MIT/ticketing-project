package com.example.ticketingproject.domain.cashCharge.entity;

import jakarta.persistence.*;
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
    private Long userId;
    private Long adminId;
    private BigDecimal amount;
    private BigDecimal balanceAfterCharge;

    protected CashCharge() {}

    private CashCharge (Long userId,
                        Long adminId,
                        BigDecimal amount,
                        BigDecimal balanceAfterCharge) {
        this.userId = userId;
        this.adminId = adminId;
        this.amount = amount;
        this.balanceAfterCharge = balanceAfterCharge;
    }

    public static CashCharge create(Long userId,
                                     Long adminId,
                                     BigDecimal amount,
                                     BigDecimal balanceAfterCharge) {
        return new CashCharge(userId, adminId, amount, balanceAfterCharge);
    }
}
