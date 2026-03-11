package com.example.ticketingproject.domain.payment.service;

import com.example.ticketingproject.domain.payment.dto.PaymentResponse;
import com.example.ticketingproject.domain.payment.entity.Payment;
import com.example.ticketingproject.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPaymentService {
    private final PaymentRepository paymentRepository;


    public Page<PaymentResponse> findAllPayment(Pageable converted) {
        Page<Payment> payments = paymentRepository.findAll(converted);

        return payments.map(PaymentResponse::from);
    }
}
