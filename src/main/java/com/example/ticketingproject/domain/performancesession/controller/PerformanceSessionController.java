package com.example.ticketingproject.domain.performancesession.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.performance.entity.PerformanceStatus;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.performancesession.service.PerformanceSessionService;
import com.example.ticketingproject.domain.work.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.example.ticketingproject.common.enums.SuccessStatus.READ_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances/{performanceId}/sessions")
public class PerformanceSessionController {

    private final PerformanceSessionService performanceSessionService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<GetSessionResponse>>> getPages(
            @PathVariable Long performanceId,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
        Page<GetSessionResponse> response = performanceSessionService.getSessions(performanceId, converted);

        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, response));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<CommonResponse<GetSessionResponse>> getDetail(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId
    ) {
        GetSessionResponse response = performanceSessionService.getSessionDetail(sessionId);
        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, response));
    }

    @GetMapping("/search")
    public ResponseEntity<CommonResponse<Page<GetSessionResponse>>> search(
            @PathVariable(name = "performanceId") Long performanceId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(required = false) PerformanceStatus status,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "1")  int page
            ) {
        Pageable converted =  PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());

        return ResponseEntity.ok(CommonResponse.success(READ_SUCCESS, performanceSessionService.search(keyword, category, startTime, endTime, status, converted)));

    }

}