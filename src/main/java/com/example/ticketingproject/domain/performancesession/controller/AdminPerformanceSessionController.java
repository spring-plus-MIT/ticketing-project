package com.example.ticketingproject.domain.performancesession.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.domain.performancesession.dto.SessionRequest;
import com.example.ticketingproject.domain.performancesession.service.AdminPerformanceSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.ticketingproject.common.enums.SuccessStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/performances/{performanceId}/sessions")
public class AdminPerformanceSessionController {

    private final AdminPerformanceSessionService adminPerformanceSessionService;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> create(
            @PathVariable Long performanceId,
            @RequestBody SessionRequest request
    ) {
        adminPerformanceSessionService.createSession(performanceId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(CREATE_SUCCESS, null));
    }

    @PatchMapping("/{sessionId}")
    public ResponseEntity<CommonResponse<Void>> update(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId,
            @RequestBody SessionRequest request
    ) {
        adminPerformanceSessionService.updateSession(sessionId, request);
        return ResponseEntity.ok(CommonResponse.success(UPDATE_SUCCESS, null));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long performanceId,
            @PathVariable Long sessionId
    ) {
        adminPerformanceSessionService.deleteSession(sessionId);
        return ResponseEntity.ok(CommonResponse.success(DELETE_SUCCESS, null));
    }

}
