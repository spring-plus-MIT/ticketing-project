package com.example.ticketingproject.domain.performance.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.performance.dto.PerformanceRequest;
import com.example.ticketingproject.domain.performance.service.AdminPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/performances")
public class AdminPerformanceController {

    private final AdminPerformanceService adminPerformanceService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> create(
            @RequestBody PerformanceRequest request
    ) {
        adminPerformanceService.createPerformance(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(CREATE_SUCCESS, null));
    }

    @PatchMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<Void>> update(
            @PathVariable Long performanceId,
            @RequestBody PerformanceRequest request
    ) {
        adminPerformanceService.updatePerformance(performanceId, request);
        return ResponseEntity.ok(CommonResponse.success(UPDATE_SUCCESS, null));
    }

    @DeleteMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<Void>> close(
            @PathVariable Long performanceId
    ) {
        adminPerformanceService.closePerformance(performanceId);
        return ResponseEntity.ok(CommonResponse.success(DELETE_SUCCESS, null));
    }

}
