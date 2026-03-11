package com.example.ticketingproject.domain.performancesession.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.performancesession.dto.SessionRequest;
import com.example.ticketingproject.domain.performancesession.service.PerformanceSessionService;
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

}