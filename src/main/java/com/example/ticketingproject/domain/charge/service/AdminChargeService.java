package com.example.ticketingproject.domain.charge.service;

import com.example.ticketingproject.domain.charge.dto.ChargeRequest;
import com.example.ticketingproject.domain.charge.dto.ChargeResponse;
import com.example.ticketingproject.domain.charge.entity.Charge;
import com.example.ticketingproject.domain.charge.repository.ChargeRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ticketingproject.common.enums.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminChargeService {

    private final ChargeRepository chargeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChargeResponse charge(Long adminId, Long userId, ChargeRequest request) {
        User admin = userRepository.findById(adminId).orElseThrow(
                () -> new UserException(ADMIN_NOT_FOUND.getHttpStatus(), ADMIN_NOT_FOUND)
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(USER_NOT_FOUND.getHttpStatus(), USER_NOT_FOUND)
        );

        user.changeBalance(request.getAmount());

        Charge charge = Charge.builder()
                .user(user)
                .admin(admin)
                .amount(request.getAmount())
                .balanceAfterCharge(user.getBalance())
                .build();

        Charge savedCharge = chargeRepository.save(charge);

        return ChargeResponse.from(savedCharge);
    }

    public Page<ChargeResponse> findAll(Pageable pageable) {
        return chargeRepository.findAll(pageable).map(ChargeResponse::from);
    }

    public Page<ChargeResponse> findAllByUserId(Long userId, Pageable pageable) {
        return chargeRepository.findAllByUserId(userId, pageable).map(ChargeResponse::from);
    }
}
