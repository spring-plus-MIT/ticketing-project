package com.example.ticketingproject.domain.performance.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.performance.dto.PerformanceRequest;
import com.example.ticketingproject.domain.performance.dto.PerformanceResponse;
import com.example.ticketingproject.domain.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances")
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> create(
            @RequestBody PerformanceRequest request
    ) {
        performanceService.createPerformance(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(CREATE_SUCCESS, null));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<PerformanceResponse>>> getPages(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
        Page<PerformanceResponse> response = performanceService.getPerformances(converted);

        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, response));
    }

    @GetMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<PerformanceResponse>> getDetail(
            @PathVariable Long performanceId
    ) {
        PerformanceResponse response = performanceService.getPerformanceDetail(performanceId);
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, response));
    }

    @PatchMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<Void>> update(
            @PathVariable Long performanceId,
            @RequestBody PerformanceRequest request
    ) {
        performanceService.updatePerformance(performanceId, request);
        return ResponseEntity.ok(CommonResponse.success(UPDATE_SUCCESS, null));
    }

    @DeleteMapping("/{performanceId}")
    public ResponseEntity<CommonResponse<Void>> close(
            @PathVariable Long performanceId
    ) {
        performanceService.closePerformance(performanceId);
        return ResponseEntity.ok(CommonResponse.success(DELETE_SUCCESS, null));
    }
}