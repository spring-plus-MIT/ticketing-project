package com.example.ticketingproject.domain.charge.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.charge.dto.ChargeResponse;
import com.example.ticketingproject.domain.charge.service.ChargeService;
import com.example.ticketingproject.security.CustomUserDetails;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.ticketingproject.common.enums.SuccessStatus.READ_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/charges")
public class ChargeController {

    private final ChargeService chargeService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<ChargeResponse>>>  findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
        ) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, chargeService.findAll(customUserDetails.getId(), pageable)));
    }
}
