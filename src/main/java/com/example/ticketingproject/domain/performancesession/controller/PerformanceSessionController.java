package com.example.ticketingproject.domain.performancesession.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.performancesession.dto.GetSessionResponse;
import com.example.ticketingproject.domain.performancesession.dto.SessionRequest;
import com.example.ticketingproject.domain.performancesession.service.PerformanceSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances")
public class PerformanceSessionController {

    private final PerformanceSessionService performanceSessionService;

    @PostMapping("/{performanceId}/sessions")
    public ResponseEntity<CommonResponse<Void>> create(
            @PathVariable Long performanceId,
            @RequestBody SessionRequest request
    ) {
        performanceSessionService.createSession(performanceId, request);
        return ResponseEntity.status(SuccessStatus.CREATED.getHttpStatus())
                .body(CommonResponse.success(
                        SuccessStatus.CREATED,
                        SuccessStatus.CREATED.getSuccessCode(),
                        SuccessStatus.CREATED.getMessage(),
                        null
                ));
    }

    @GetMapping("/{performanceId}/sessions")
    public ResponseEntity<CommonResponse<Page<GetSessionResponse>>> getPages(
            @PathVariable Long performanceId,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable converted = PageRequest.of(page - 1, pageable.getPageSize(), pageable.getSort());
        Page<GetSessionResponse> response = performanceSessionService.getSessions(performanceId, converted);

        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.GET_SUCCESS,
                        SuccessStatus.GET_SUCCESS.getSuccessCode(),
                        SuccessStatus.GET_SUCCESS.getMessage(),
                        response
                ));
    }

    @GetMapping("/{performanceId}/sessions/{sessionId}")
    public ResponseEntity<CommonResponse<GetSessionResponse>> getDetail(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId
    ) {
        GetSessionResponse response = performanceSessionService.getSessionDetail(sessionId);
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.GET_SUCCESS,
                        SuccessStatus.GET_SUCCESS.getSuccessCode(),
                        SuccessStatus.GET_SUCCESS.getMessage(),
                        response
                ));
    }

    @PatchMapping("/{performanceId}/sessions/{sessionId}")
    public ResponseEntity<CommonResponse<Void>> update(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @RequestBody SessionRequest request
    ) {
        performanceSessionService.updateSession(sessionId, request);
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.PROCESS_SUCCESS,
                        SuccessStatus.PROCESS_SUCCESS.getSuccessCode(),
                        SuccessStatus.PROCESS_SUCCESS.getMessage(),
                        null
                ));
    }

    @DeleteMapping("/{performanceId}/sessions/{sessionId}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId
    ) {
        performanceSessionService.deleteSession(sessionId);
        return ResponseEntity.ok(
                CommonResponse.success(
                        SuccessStatus.PROCESS_SUCCESS,
                        SuccessStatus.PROCESS_SUCCESS.getSuccessCode(),
                        SuccessStatus.PROCESS_SUCCESS.getMessage(),
                        null
                ));
    }
}