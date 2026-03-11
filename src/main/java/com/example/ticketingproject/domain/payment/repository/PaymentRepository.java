package com.example.ticketingproject.domain.payment.repository;

import com.example.ticketingproject.domain.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findAllByUserId(Long userId, Pageable converted);

    Optional<Payment> findByIdAndUserId(Long paymentId, Long userId);

    boolean existsByReservationId(Long reservationId);
}
