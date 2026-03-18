package com.example.ticketingproject.common.search.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.search.dto.PerformanceSearchResponse;
import com.example.ticketingproject.domain.performance.enums.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.service.PerformanceSessionService;
import com.example.ticketingproject.domain.work.enums.Category;
import com.example.ticketingproject.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.example.ticketingproject.common.enums.SuccessStatus.READ_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performance/search")
public class PerformanceSearchController {

    private final PerformanceSessionService performanceSessionService;

    @GetMapping("/v1")
    public ResponseEntity<CommonResponse<Page<PerformanceSearchResponse>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) PerformanceStatus status,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "1")  int page,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Pageable converted =  PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, performanceSessionService.search(keyword, category, startDate, endDate, status, converted, customUserDetails.getId())));
    }

    @GetMapping("/v2")
    public ResponseEntity<CommonResponse<Page<PerformanceSearchResponse>>> searchV2(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) PerformanceStatus status,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "1") int page,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, performanceSessionService.searchV2(keyword, category, startDate, endDate, status, converted, customUserDetails.getId())));
    }
}
