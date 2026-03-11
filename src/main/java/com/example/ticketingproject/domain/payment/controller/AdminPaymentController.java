package com.example.ticketingproject.domain.payment.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.payment.dto.PaymentResponse;
import com.example.ticketingproject.domain.payment.service.AdminPaymentService;
import com.example.ticketingproject.domain.payment.service.PaymentService;
import com.example.ticketingproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/payments")
public class AdminPaymentController {
    private final AdminPaymentService adminPaymentService;
    private final PaymentService paymentService;

    @GetMapping("/{paymentId}/{userId}")
    public ResponseEntity<CommonResponse<PaymentResponse>> getOnePayment (
            @PathVariable Long paymentId, @PathVariable Long userId
    ) {
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.READ_SUCCESS, paymentService.findOnePayment(paymentId, userId)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<PaymentResponse>>> getAllPayment(
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable converted = PageRequest.of(
                page - 1,
                pageable.getPageSize(),
                pageable.getSort()
        );

        return ResponseEntity.ok(CommonResponse.success(
                        SuccessStatus.READ_SUCCESS,
                        adminPaymentService.findAllPayment(converted)
                )
        );
    }
}
