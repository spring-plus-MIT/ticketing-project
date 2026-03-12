package com.example.ticketingproject.domain.payment.service;

import com.example.ticketingproject.auth.exception.AuthException;
import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.payment.dto.CreatePaymentRequest;
import com.example.ticketingproject.domain.payment.dto.PaymentResponse;
import com.example.ticketingproject.domain.payment.entity.Payment;
import com.example.ticketingproject.domain.payment.enums.PaymentStatus;
import com.example.ticketingproject.domain.payment.exception.PaymentException;
import com.example.ticketingproject.domain.payment.repository.PaymentRepository;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request, Long userId) {
        Reservation reservation = reservationRepository.findByIdAndUserId(
                request.getReservationId(), userId).orElseThrow(
                () -> new AuthException(
                        ErrorStatus.ACCESS_FORBIDDEN.getHttpStatus(),
                        ErrorStatus.ACCESS_FORBIDDEN
                )
        );

        boolean alreadyPaid = paymentRepository.existsByReservationId(reservation.getId());
        reservation.validateNotPaid(alreadyPaid);

        User user = reservation.getUser();

        BigDecimal balanceAfterPayment = user.pay(request.getAmount());

        reservation.confirm();

        Payment payment = Payment.builder()
                .reservation(reservation)
                .user(user)
                .amount(request.getAmount())
                .paymentStatus(PaymentStatus.SUCCESS)
                .balanceAfterPayment(balanceAfterPayment)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.from(savedPayment);
    }

    public PaymentResponse findOnePayment(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new PaymentException(
                        ErrorStatus.PAYMENT_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.PAYMENT_NOT_FOUND
                )
        );

        return PaymentResponse.from(payment);
    }

    public Page<PaymentResponse> findAllPayments(Long userId, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAllByUserId(userId, pageable);

        return payments.map(PaymentResponse::from);
    }
}
