package com.example.ticketingproject.domain.payment.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.payment.dto.CreatePaymentRequest;
import com.example.ticketingproject.domain.payment.dto.PaymentResponse;
import com.example.ticketingproject.domain.payment.service.PaymentService;
import com.example.ticketingproject.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CommonResponse<PaymentResponse>> createPayment (
            @Valid @RequestBody CreatePaymentRequest request,
            CustomUserDetails customUserDetails
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(
                SuccessStatus.CREATE_SUCCESS,
                paymentService.createPayment(request, customUserDetails.getId())
                )
        );
    }

    @GetMapping("/{paymentId}/{userId}")
    public ResponseEntity<CommonResponse<PaymentResponse>> getOnePayment (@PathVariable Long paymentId, CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(CommonResponse.success(
                SuccessStatus.READ_SUCCESS,
                paymentService.findOnePayment(paymentId, customUserDetails.getId())
                )
        );
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<PaymentResponse>>> getAllPayments(
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page,
            CustomUserDetails customUserDetails
    ) {
        Pageable converted = PageRequest.of(
                page - 1,
                pageable.getPageSize(),
                pageable.getSort()
        );

        return ResponseEntity.ok(CommonResponse.success(
                SuccessStatus.READ_SUCCESS,
                paymentService.findAllPayments(customUserDetails.getId(), converted)
                )
        );
    }
}
