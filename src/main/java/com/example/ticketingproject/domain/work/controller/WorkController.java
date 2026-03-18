package com.example.ticketingproject.domain.work.controller;

import com.example.ticketingproject.common.dto.CommonResponse;
import com.example.ticketingproject.common.enums.SuccessStatus;
import com.example.ticketingproject.domain.work.dto.WorkResponse;
import com.example.ticketingproject.domain.work.service.WorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/works")
public class WorkController {
    private final WorkService workService;

    @GetMapping
    public ResponseEntity<CommonResponse<Page<WorkResponse>>> getWorks (
            @PageableDefault Pageable pageable,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable converted = PageRequest.of(
                page - 1,
                pageable.getPageSize(),
                pageable.getSort()
        );
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.READ_SUCCESS, workService.findAllWork(converted)));
    }

    @GetMapping("/{workId}")
    public ResponseEntity<CommonResponse<WorkResponse>> getOneWork(@PathVariable Long workId) {
        return ResponseEntity.ok(CommonResponse.success(SuccessStatus.READ_SUCCESS, workService.findOneWork(workId)));
    }
}
