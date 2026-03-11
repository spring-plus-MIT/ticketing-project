package com.example.ticketingproject.domain.work.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.work.dto.CreateWorkRequest;
import com.example.ticketingproject.domain.work.dto.WorkResponse;
import com.example.ticketingproject.domain.work.dto.UpdateWorkRequest;
import com.example.ticketingproject.domain.work.service.AdminWorkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/works")
public class AdminWorkController {
    private final AdminWorkService adminWorkService;

    @PostMapping
    public ResponseEntity<CommonResponse<WorkResponse>> createWork (@Valid @RequestBody CreateWorkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonResponse.success(SuccessStatus.CREATE_SUCCESS, adminWorkService.createWork(request)));
    }

    @PutMapping("/{workId}")
    public ResponseEntity<CommonResponse<WorkResponse>> updateWork (
            @PathVariable Long workId,
            @Valid @RequestBody UpdateWorkRequest request
    ) {
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.UPDATE_SUCCESS, adminWorkService.updateWork(workId, request)));
    }
}
