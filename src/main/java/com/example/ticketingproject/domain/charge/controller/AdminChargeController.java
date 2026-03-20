package com.example.ticketingproject.domain.charge.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.charge.dto.ChargeRequest;
import com.example.ticketingproject.domain.charge.dto.ChargeResponse;
import com.example.ticketingproject.domain.charge.service.AdminChargeService;
import com.example.ticketingproject.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/charges")
public class AdminChargeController {

    private final AdminChargeService adminChargeService;

    @PostMapping("/{userId}")
    public ResponseEntity<CommonResponse<ChargeResponse>> charge(@PathVariable(name = "userId") Long userId, @Valid @RequestBody ChargeRequest request, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success(CHARGE_SUCCESS, adminChargeService.charge(customUserDetails.getId(), userId, request)));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<ChargeResponse>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, adminChargeService.findAll(pageable)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CommonResponse<Page<ChargeResponse>>> getAllByUserId(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, adminChargeService.findAllByUserId(userId, pageable)));
    }
}
