package com.example.ticketingproject.domain.charge.service;

import com.example.ticketingproject.domain.charge.dto.ChargeResponse;
import com.example.ticketingproject.domain.charge.repository.ChargeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChargeService {

    private final ChargeRepository chargeRepository;

    public Page<ChargeResponse> findAll(Long userId, Pageable pageable) {
        return chargeRepository.findAllByUserId(userId, pageable).map(ChargeResponse::from);
    }
}
