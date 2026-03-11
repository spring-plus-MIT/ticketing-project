package com.example.ticketingproject.domain.payment.service;

import com.example.ticketingproject.common.enums.ErrorStatus;
import com.example.ticketingproject.domain.payment.dto.CreatePaymentRequest;
import com.example.ticketingproject.domain.payment.dto.PaymentResponse;
import com.example.ticketingproject.domain.payment.entity.Payment;
import com.example.ticketingproject.domain.payment.enums.PaymentStatus;
import com.example.ticketingproject.domain.payment.exception.PaymentException;
import com.example.ticketingproject.domain.payment.repository.PaymentRepository;
import com.example.ticketingproject.domain.reservation.entity.Reservation;
import com.example.ticketingproject.domain.reservation.enums.ReservationStatus;
import com.example.ticketingproject.domain.reservation.exception.ReservationException;
import com.example.ticketingproject.domain.reservation.repository.ReservationRepository;
import com.example.ticketingproject.domain.user.entity.User;
import com.example.ticketingproject.domain.user.exception.UserException;
import com.example.ticketingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                )
        );

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ReservationException(ErrorStatus.RESERVATION_NOT_FOUND));

        Payment payment = Payment.builder()
                .reservation(reservation)
                .user(user)
                .amount(request.getAmount())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        reservation.updateStatus(ReservationStatus.CONFIRMED);

        return PaymentResponse.from(savedPayment);
    }

    public PaymentResponse findOnePayment(Long paymentId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                )
        );

        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new PaymentException(
                        ErrorStatus.PAYMENT_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.PAYMENT_NOT_FOUND
                )
        );
        return PaymentResponse.from(payment);
    }

    public Page<PaymentResponse> findAllPayment(Long userId, Pageable converted) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(
                        ErrorStatus.USER_NOT_FOUND.getHttpStatus(),
                        ErrorStatus.USER_NOT_FOUND
                )
        );

        Page<Payment> payments = paymentRepository.findAll(converted);

        return payments.map(PaymentResponse::from);
    }
}
