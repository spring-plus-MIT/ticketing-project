package com.example.ticketingproject.domain.charge.dto;

import com.example.ticketingproject.domain.charge.entity.Charge;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ChargeResponse {

    private final Long chargeId;
    private final Long userId;
    private final Long adminId;
    private final BigDecimal amount;
    private final BigDecimal balanceAfterCharge;

    public static ChargeResponse from(Charge charge) {
        return ChargeResponse.builder()
                .chargeId(charge.getId())
                .userId(charge.getUser().getId())
                .adminId(charge.getAdmin().getId())
                .amount(charge.getAmount())
                .balanceAfterCharge(charge.getBalanceAfterCharge())
                .build();
    }
}
